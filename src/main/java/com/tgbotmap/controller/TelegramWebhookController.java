package com.tgbotmap.controller;

import com.tgbotmap.config.TelegramBotProperties;
import com.tgbotmap.model.telegram.Message;
import com.tgbotmap.model.telegram.Update;
import com.tgbotmap.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhook/telegram")
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final TelegramBotProperties botProperties;
    private final BotService botService;

    @PostMapping
    public ResponseEntity<Void> onUpdate(@RequestBody Update update) {
        Message message = update.getMessage();

        if (message == null || message.getChat() == null) {
            log.debug("Received update without message or chat, ignoring. updateId={}", update.getUpdateId());
            return ResponseEntity.ok().build();
        }

        Long chatId = message.getChat().getId();
        String text = message.getText();

        // Check if the message is from the allowed group chat
        Long allowedGroupId = botProperties.getAllowedGroupId();
        if (allowedGroupId != null && !allowedGroupId.equals(chatId)) {
            log.warn("Rejected message from unauthorized chatId={}. Allowed groupId={}", chatId, allowedGroupId);
            return ResponseEntity.ok().build();
        }

        // Check if text is present and not blank
        if (text == null || text.isBlank()) {
            log.debug("Received message with no text from chatId={}, ignoring", chatId);
            return ResponseEntity.ok().build();
        }

        log.info("Accepted message from chatId={}: {}", chatId, text);
        botService.processUpdate(update);

        return ResponseEntity.ok().build();
    }
}
