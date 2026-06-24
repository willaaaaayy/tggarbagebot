package com.tgbotmap.controller;

import com.tgbotmap.config.TelegramBotProperties;
import com.tgbotmap.model.telegram.Message;
import com.tgbotmap.model.telegram.Update;
import com.tgbotmap.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Single entry point for Telegram webhook updates.
 *
 * <p>Performs, in order: secret-token verification, allowed-group filtering, then delegates
 * accepted updates to {@link BotService}. Always returns 200 to Telegram for accepted-but-ignored
 * updates so they are not redelivered; only an invalid secret yields 403.
 */
@Slf4j
@RestController
@RequestMapping("${telegram.webhook.path:/api/webhook}")
@RequiredArgsConstructor
public class WebhookController {

    static final String SECRET_TOKEN_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final TelegramBotProperties botProperties;
    private final BotService botService;

    @PostMapping
    public ResponseEntity<Void> onUpdate(
            @RequestBody Update update,
            @RequestHeader(value = SECRET_TOKEN_HEADER, required = false) String secretToken) {

        String expectedSecret = botProperties.getWebhookSecret();
        if (expectedSecret != null && !expectedSecret.isBlank() && !expectedSecret.equals(secretToken)) {
            log.warn("Rejected webhook call with invalid secret token. updateId={}", update.getUpdateId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Message message = update.getMessage();
        if (message == null || message.getChat() == null) {
            log.debug("Received update without message or chat, ignoring. updateId={}", update.getUpdateId());
            return ResponseEntity.ok().build();
        }

        Long chatId = message.getChat().getId();
        Long allowedGroupId = botProperties.getAllowedGroupId();
        if (allowedGroupId != null && !allowedGroupId.equals(chatId)) {
            log.warn("Rejected message from unauthorized chatId={}. Allowed groupId={}", chatId, allowedGroupId);
            return ResponseEntity.ok().build();
        }

        log.debug("Accepted update {} from chatId={}", update.getUpdateId(), chatId);
        botService.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
