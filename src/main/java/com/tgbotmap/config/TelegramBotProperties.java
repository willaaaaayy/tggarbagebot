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

    /**
     * Optional secret token. When set, it is registered with Telegram via {@code setWebhook}
     * and every incoming update must carry a matching {@code X-Telegram-Bot-Api-Secret-Token}
     * header. Leave blank to disable verification.
     */
    private String webhookSecret;
}
