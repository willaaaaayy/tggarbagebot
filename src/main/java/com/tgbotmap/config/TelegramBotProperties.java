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
}
