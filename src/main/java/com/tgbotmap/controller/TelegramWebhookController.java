package com.tgbotmap.controller;

import com.tgbotmap.config.TelegramBotProperties;
import com.tgbotmap.dto.telegram.TelegramMessage;
import com.tgbotmap.dto.telegram.TelegramUpdate;
import com.tgbotmap.service.AddressService;
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
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<Void> onUpdate(@RequestBody TelegramUpdate update) {
        TelegramMessage message = update.getMessage();

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
        addressService.processMessage(text);

        return ResponseEntity.ok().build();
    }
}
