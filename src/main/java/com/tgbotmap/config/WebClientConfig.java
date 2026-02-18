package com.tgbotmap.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.api.base-url:https://api.telegram.org}")
    private String baseUrl;

    @Value("${telegram.api.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${telegram.api.response-timeout-ms:10000}")
    private int responseTimeoutMs;

    @Bean
    public WebClient telegramWebClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs));

        return builder
                .baseUrl(baseUrl + "/bot" + botToken)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
