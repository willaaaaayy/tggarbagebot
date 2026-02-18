package com.tgbotmap.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class TelegramSenderService {

    private final String chatId;
    private final String botToken;

    private WebClient webClient;

    public TelegramSenderService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.chat-id}") String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @PostConstruct
    void init() {
        String baseUrl = "https://api.telegram.org/bot" + botToken;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        log.info("TelegramSenderService initialized with base URL: https://api.telegram.org/bot***");
    }

    /**
     * Sends a location message with a clickable Yandex Maps link to the configured Telegram chat.
     *
     * @param mapLink the Yandex Maps URL to include in the message
     */
    public void sendLocationMessage(String mapLink) {
        try {
            String text = "📍 Место добавлено\n🗺 [Открыть карту](" + mapLink + ")";

            Map<String, String> requestBody = Map.of(
                    "chat_id", chatId,
                    "text", text,
                    "parse_mode", "Markdown"
            );

            webClient.post()
                    .uri("/sendMessage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.info("Telegram message sent successfully to chat {}", chatId),
                            error -> log.error("Failed to send Telegram message to chat {}: {}", chatId, error.getMessage(), error)
                    );
        } catch (Exception e) {
            log.error("Error building Telegram sendMessage request: {}", e.getMessage(), e);
        }
    }
}
