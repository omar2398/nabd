package com.nabd.insight_service.service;

import com.nabd.insight_service.client.UsageClient;
import com.nabd.insight_service.dto.InsightDto;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class InsightService {
  private final UsageClient usageClient;

  public InsightService(UsageClient usageClient) {
    this.usageClient = usageClient;
  }

  public InsightDto getSavingTips(Long userId) {
    final var usageResponse = usageClient.getOverviewForXDays(userId, 3);

  }
}
