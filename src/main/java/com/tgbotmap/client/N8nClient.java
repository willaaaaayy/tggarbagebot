package com.tgbotmap.client;

import com.tgbotmap.exception.GeocodingClientException;
import com.tgbotmap.exception.GeocodingException;
import com.tgbotmap.exception.GeocodingServerException;
import com.tgbotmap.exception.GeocodingTimeoutException;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class N8nClient {

    private final WebClient webClient;

    public N8nClient(@Value("${n8n.url}") String n8nUrl) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .responseTimeout(Duration.ofSeconds(5));

        this.webClient = WebClient.builder()
                .baseUrl(n8nUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * Sends an address to the N8N geocoding webhook and returns the geocoded coordinates.
     *
     * @param address the address string to geocode
     * @return a {@link Mono} emitting the {@link GeocodeResponse} with lat/lon coordinates
     * @throws GeocodingClientException  on 4xx responses
     * @throws GeocodingServerException  on 5xx responses (after retries exhausted)
     * @throws GeocodingTimeoutException on request timeout
     * @throws GeocodingException        on any other geocoding failure
     */
    public Mono<GeocodeResponse> geocode(String address) {
        log.debug("Sending geocode request for address: {}", address);

        return webClient.post()
                .uri("/webhook/geocode")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new GeocodeRequest(address))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.warn("N8N geocode returned 4xx: status={}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new GeocodingClientException(
                                    "Geocoding client error: " + response.statusCode() + " — " + body,
                                    response.statusCode()
                            )));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("N8N geocode returned 5xx: status={}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new GeocodingServerException(
                                    "Geocoding server error: " + response.statusCode() + " — " + body
                            )));
                })
                .bodyToMono(GeocodeResponse.class)
                .doOnNext(resp -> log.debug("Geocode response: lat={}, lon={}", resp.lat(), resp.lon()))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(500))
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((spec, signal) -> {
                            log.error("Geocode retries exhausted for address: {}", address);
                            return new GeocodingException(
                                    "Geocoding failed after retries for address: " + address,
                                    signal.failure()
                            );
                        })
                )
                .onErrorMap(TimeoutException.class, ex -> {
                    log.error("Geocode request timed out for address: {}", address);
                    return new GeocodingTimeoutException("Geocoding timed out for address: " + address, ex);
                })
                .onErrorMap(ex -> !(ex instanceof GeocodingException), ex -> {
                    log.error("Unexpected geocoding error for address: {}", address, ex);
                    return new GeocodingException("Geocoding failed for address: " + address, ex);
                });
    }

    private boolean isRetryable(Throwable throwable) {
        if (throwable instanceof GeocodingServerException) {
            return true;
        }
        if (throwable instanceof GeocodingClientException) {
            return false;
        }
        if (throwable instanceof WebClientResponseException wcre) {
            return wcre.getStatusCode().is5xxServerError();
        }
        // Retry on transport errors (connection refused, timeout, etc.)
        return !(throwable instanceof GeocodingClientException);
    }

    /**
     * Request body for the N8N geocode webhook.
     */
    public record GeocodeRequest(String address) {
    }

    /**
     * Response from the N8N geocode webhook.
     */
    public record GeocodeResponse(double lat, double lon) {
    }
}
