package com.tgbotmap.service;

import com.tgbotmap.config.TelegramBotProperties;
import com.tgbotmap.model.telegram.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.annotation.PostConstruct;
import java.util.Map;

/**
 * Service responsible for registering the webhook with Telegram API on application startup.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "telegram.bot.webhook-url")
public class WebhookRegistrationService {

    private final WebClient telegramWebClient;
    private final TelegramBotProperties botProperties;

    /**
     * Registers the webhook with Telegram API on application startup.
     * This method is called automatically after the bean is initialized.
     */
    @PostConstruct
    public void registerWebhook() {
        String webhookUrl = botProperties.getWebhookUrl();
        
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.info("No webhook URL configured, skipping webhook registration");
            return;
        }

        log.info("Registering Telegram webhook: {}", webhookUrl);

        try {
            TelegramResponse response = telegramWebClient.post()
                    .uri("/setWebhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("url", webhookUrl))
                    .retrieve()
                    .bodyToMono(TelegramResponse.class)
                    .block();

            if (response != null && Boolean.TRUE.equals(response.getOk())) {
                log.info("Successfully registered Telegram webhook: {}", webhookUrl);
            } else {
                log.warn("Telegram webhook registration returned not-ok response: {}", response);
            }
        } catch (WebClientResponseException e) {
            log.error("Telegram API error while registering webhook [{}]: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Failed to register Telegram webhook: {}", webhookUrl, e);
        }
    }
}
