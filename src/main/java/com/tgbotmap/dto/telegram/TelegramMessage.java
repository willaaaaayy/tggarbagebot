package com.tgbotmap.dto.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramMessage {

    @JsonProperty("message_id")
    private Long messageId;

    @JsonProperty("chat")
    private TelegramChat chat;

    @JsonProperty("text")
    private String text;

    @JsonProperty("date")
    private Long date;
}
