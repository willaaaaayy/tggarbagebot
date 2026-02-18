package com.tgbotmap.model.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackQuery {

    @JsonProperty("id")
    private String id;

    @JsonProperty("from")
    private TelegramUser from;

    @JsonProperty("message")
    private Message message;

    @JsonProperty("data")
    private String data;
}
