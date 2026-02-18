package com.tgbotmap.model.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramResponse {

    @JsonProperty("ok")
    private Boolean ok;

    @JsonProperty("description")
    private String description;

    @JsonProperty("result")
    private Object result;
}
