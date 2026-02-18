package com.tgbotmap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotProperties {

    /**
     * Telegram Bot API token.
     */
    private String token;

    /**
     * Allowed group chat ID. Only messages from this chat will be processed.
     */
    private Long allowedGroupId;

    /**
     * Webhook URL for Telegram to send updates to.
     * Should be the full URL including the webhook path (e.g., https://example.com/api/webhook)
     */
    private String webhookUrl;
}
