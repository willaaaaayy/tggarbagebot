package com.tgbotmap.model.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Update {

    @JsonProperty("update_id")
    private Long updateId;

    @JsonProperty("message")
    private Message message;

    @JsonProperty("callback_query")
    private CallbackQuery callbackQuery;
}
