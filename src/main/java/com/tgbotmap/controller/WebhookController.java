package com.tgbotmap.controller;

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
@RequestMapping("${telegram.webhook.path:/api/webhook}")
@RequiredArgsConstructor
public class WebhookController {

    private final BotService botService;

    @PostMapping
    public ResponseEntity<Void> onUpdate(@RequestBody Update update) {
        log.debug("Received update: {}", update.getUpdateId());
        botService.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
