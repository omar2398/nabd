package com.nabd.usage_service.servicee;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.nabd.kafka.event.AlertingEvent;
import com.nabd.kafka.event.EnergyUsageEvent;
import com.nabd.usage_service.client.DeviceClient;
import com.nabd.usage_service.client.UserClient;
import com.nabd.usage_service.dto.DeviceDto;
import com.nabd.usage_service.dto.DeviceEnergy;
import com.nabd.usage_service.dto.UsageDto;
import com.nabd.usage_service.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsageService {
  private final InfluxDBClient influxDBClient;
  private final DeviceClient deviceClient;
  private final UserClient userClient;
  private final KafkaTemplate<String, AlertingEvent> kafka;

  @Value("${influxdb.bucket}")
  private String influxBucket;

  @Value("${influxdb.org}")
  private String influxOrg;

  public UsageService(
      InfluxDBClient influxDBClient,
      DeviceClient deviceClient,
      UserClient userClient,
      KafkaTemplate kafka) {
    this.influxDBClient = influxDBClient;
    this.deviceClient = deviceClient;
    this.userClient = userClient;
    this.kafka = kafka;
  }

  @KafkaListener(topics = "energy-usage", groupId = "usage-service")
  public void energyUsageEvent(EnergyUsageEvent energyUsageEvent) {
    // log.info("Received energy usage event: {}", energyUsageEvent);
    Point point =
        Point.measurement("energy-usage")
            .addTag("deviceId", String.valueOf(energyUsageEvent.deviceId()))
            .addField("energyConsumed", energyUsageEvent.energyConsumed())
            .time(energyUsageEvent.timestamp(), WritePrecision.MS);
    influxDBClient.getWriteApiBlocking().writePoint(influxBucket, influxOrg, point);
    log.info("The record was stored into influxdb: {}", point.getFields());
  }

  @Scheduled(
      cron = "*/10 * * * * *") // Actually this will be once per day, but this for the dev purposes.
  public void scheduledTask() {
    log.info("Scheduled job has started");
    final Instant now = Instant.now();
    final Instant oneHourAgo = now.minusSeconds(3600);

    String fluxQuery =
        String.format(
            """
              from(bucket: "%s")
                |> range(start: time(v: "%s"), stop: time(v: "%s"))
                |> filter(fn: (r) => r["_measurement"] == "energy_usage")
                |> filter(fn: (r) => r["_field"] == "energyConsumed")
                |> group(columns: ["deviceId"])
                |> sum(column: "_value")
              """,
            influxBucket, oneHourAgo.toString(), now);

    QueryApi queryApi = influxDBClient.getQueryApi();
    List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);
    List<DeviceEnergy> deviceEnergies = new ArrayList<>();
    log.info("the deviceEnergies are {}", deviceEnergies);

    for (FluxTable table : tables) {
      for (FluxRecord record : table.getRecords()) {
        String deviceIdStr = (String) record.getValueByKey("deviceId");
        Double energyConsumed =
            record.getValueByKey("_value") instanceof Number
                ? ((Number) record.getValueByKey("_value")).doubleValue()
                : 0.0;
        deviceEnergies.add(
            DeviceEnergy.builder()
                .deviceId(Long.valueOf(deviceIdStr))
                .energyConsumed(energyConsumed)
                .build());
      }
      log.info("Device Energies over thee postt hour are: {}", deviceEnergies);

      for (DeviceEnergy deviceEnergy : deviceEnergies) {
        final DeviceDto deviceDto = deviceClient.getDeviceById(deviceEnergy.getDeviceId());
        if (deviceDto == null) {
          log.warn("Device nott found: {}", deviceEnergy.getDeviceId());
        }
        deviceEnergy.setUserId(deviceDto.userId());
      }

      deviceEnergies.removeIf(de -> de.getUserId() == null);

      Map<Long, List<DeviceEnergy>> userDeviceEnergyMap =
          deviceEnergies.stream().collect(Collectors.groupingBy(DeviceEnergy::getUserId));

      List<Long> userIds = new ArrayList<>(userDeviceEnergyMap.keySet());
      final Map<Long, Double> userThresholdMap = new HashMap<>();
      final Map<Long, String> userEmailMap = new HashMap<>();

      for (final long userId : userIds) {
        try {
          UserDto user = userClient.getUserById(userId);
          if (user == null || !user.alerting()) {
            log.warn("User not found, or the user stoped the alerting {}", user.id());
            continue;
          }
          userThresholdMap.put(userId, user.energyAlertingThreshold());
          userEmailMap.put(userId, user.email());
        } catch (Exception e) {
          log.error("Failed to fetch user for id : {}", userId);
        }
      }
      log.info("user threshold map: {}", userThresholdMap);
      final List<Long> alertedUsers = new ArrayList<>(userThresholdMap.keySet());
      for (final Long userId : alertedUsers) {
        final Double threshold = userThresholdMap.get(userId);
        final List<DeviceEnergy> devices = userDeviceEnergyMap.get(userId);
        final Double totalConsumptionByUser =
            devices.stream().mapToDouble(DeviceEnergy::getEnergyConsumed).sum();
        if (totalConsumptionByUser > threshold) {
          log.info(
              "the user {} has exceed  the energy threshold with total consumption {}, since the threshold is {}",
              userId,
              totalConsumptionByUser,
              threshold);
          final AlertingEvent alertingEvent =
              AlertingEvent.builder()
                  .userId(userId)
                  .message("Energy conumption threshold exceeded")
                  .energyConsumed(totalConsumptionByUser)
                  .threshold(threshold)
                  .email(userEmailMap.get(userId))
                  .build();
          kafka.send("energy-alerts", alertingEvent);
        } else {
          log.info(
              "User with id {} with the threshold {} hasn't exceed the threshold yet wwith {}",
              userId,
              threshold,
              totalConsumptionByUser);
        }
      }
    }
  }

  public UsageDto getOverview(Long userId, int days) {
    log.info("Get the usage for userid {} over past {} days", userId, days);
    final var devices = deviceClient.getAllDevicesByUser(userId);
    if (devices == null || devices.isEmpty()) {
      return UsageDto.builder()
              .userId(userId)
              .devices(List.of())
              .build();
    }
    List<String> deviceIds = devices.stream()
            .map(d -> d.id().toString())
            .toList();

    final Instant now = Instant.now();
    final Instant start = now.minusSeconds((long) days * 24 * 3600);
    String deviceFilter = deviceIds.stream()
            .map(id -> "\"" + id + "\"")
            .collect(Collectors.joining(", "));
    String fluxQuery =
            String.format(
                    """
                    from(bucket: "%s")
                      |> range(start: time(v: "%s"), stop: time(v: "%s"))
                      |> filter(fn: (r) => r["_measurement"] == "energy-usage")
                      |> filter(fn: (r) => r["_field"] == "energyConsumed")
                      |> filter(fn: (r) => contains(value: r["deviceId"], set: [%s]))
                      |> group(columns: ["deviceId"])
                      |> sum(column: "_value")
                    """,
                    influxBucket,
                    start,
                    now,
                    deviceFilter);
    log.info("Flux query: {}", fluxQuery);
    final Map<Long, Double> aggregatedMap = new HashMap<>();
    QueryApi queryApi = influxDBClient.getQueryApi();
    List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);
    for (FluxTable table : tables) {
      for (FluxRecord record : table.getRecords()) {
        Object deviceIdValue = record.getValueByKey("deviceId");
        Object energyValue = record.getValueByKey("_value");

        if (deviceIdValue == null) {
          continue;
        }

        Long deviceId = Long.valueOf(deviceIdValue.toString());

        Double energyConsumed =
                energyValue instanceof Number
                        ? ((Number) energyValue).doubleValue()
                        : 0.0;

        aggregatedMap.put(deviceId, energyConsumed);
      }
    }

    log.info("Aggregated energy consumption: {}", aggregatedMap);

    List<DeviceEnergy> deviceEnergies = devices.stream()
            .map(device -> DeviceEnergy.builder()
                    .deviceId(device.id())
                    .userId(userId)
                    .energyConsumed(
                            aggregatedMap.getOrDefault(device.id(), 0.0))
                    .build())
            .toList();

    return UsageDto.builder()
            .userId(userId)
            .devices(devices)
            .build();
  }
}
