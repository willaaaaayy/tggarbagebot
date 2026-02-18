package com.tgbotmap.integration;

import com.tgbotmap.model.telegram.SendMessageRequest;
import com.tgbotmap.model.telegram.TelegramResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramApiClient {

    private final WebClient telegramWebClient;

    /**
     * Sends a text message to the specified chat.
     *
     * @param chatId Telegram chat ID
     * @param text   message text
     */
    public void sendMessage(Long chatId, String text) {
        SendMessageRequest request = SendMessageRequest.builder()
                .chatId(chatId)
                .text(text)
                .build();

        sendMessage(request);
    }

    /**
     * Sends a message using a fully constructed request.
     *
     * @param request the send message request
     */
    public void sendMessage(SendMessageRequest request) {
        try {
            TelegramResponse response = telegramWebClient.post()
                    .uri("/sendMessage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TelegramResponse.class)
                    .block();

            if (response != null && Boolean.TRUE.equals(response.getOk())) {
                log.debug("Message sent successfully to chat {}", request.getChatId());
            } else {
                log.warn("Telegram API returned not-ok response for chat {}: {}",
                        request.getChatId(), response);
            }
        } catch (WebClientResponseException e) {
            log.error("Telegram API error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Failed to send message to chat {}", request.getChatId(), e);
        }
    }
}
