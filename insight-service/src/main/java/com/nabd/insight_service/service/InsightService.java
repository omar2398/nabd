package com.nabd.insight_service.service;

import com.nabd.insight_service.client.UsageClient;
import com.nabd.insight_service.dto.InsightDto;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsightService {
  private final UsageClient usageClient;
  private final OllamaChatModel ollamaChatModel;

  public InsightService(UsageClient usageClient, OllamaChatModel ollamaChatModel) {
    this.usageClient = usageClient;
    this.ollamaChatModel = ollamaChatModel;
  }

  public InsightDto getOverView(Long userId) {
    final var usageResponse = usageClient.getOverviewForXDays(userId, 3);
    double totalUsage = usageResponse.devices().stream().mapToDouble(e -> e.energyConsumed()).sum();
    log.info(
        "the total usage is {}, and currently I will call the  ollama for the the userId {}",
        totalUsage,
        userId);
    String prompt =
        new StringBuilder()
            .append("Analyze the following energy usage data and provide a ")
            .append("concise overview with actionable insights.")
            .append("This data is the aggregate data for the past 3 days.")
            .append("Usage Data:\n")
            .append(usageResponse.devices())
            .toString();
    Prompt actualPrompt = new Prompt(prompt);
    ChatResponse aiResponse = ollamaChatModel.call(actualPrompt);
    return InsightDto.builder()
        .userId(userId)
        .tips(aiResponse.getResult().getOutput().getText())
        .energyUsage(totalUsage)
        .build();
  }
  public InsightDto getSavingTips(Long userId) {
    final var usageResponse = usageClient.getOverviewForXDays(userId, 3);
    double totalUsage = usageResponse.devices().stream().mapToDouble(e -> e.energyConsumed()).sum();
    log.info(
            "the total usage is {}, and currently I will call the ollama for the the userId {}",
            totalUsage,
            userId);
    String prompt = new StringBuilder()
            .append("This is my total consumption over the past 3 days. ")
            .append("How can I reduce my energy consumption? ")
            .append("How does it compare to average households?\n")
            .append("Total energy used: ")
            .append(totalUsage)
            .toString();

    Prompt actualPrompt = new Prompt(prompt);
    ChatResponse aiResponse = ollamaChatModel.call(actualPrompt);
    return InsightDto.builder()
            .userId(userId)
            .tips(aiResponse.getResult().getOutput().getText())
            .energyUsage(totalUsage)
            .build();
  }
}
