package com.tgbotmap.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Thrown when the geocoding service returns a 4xx client error.
 */
public class GeocodingClientException extends GeocodingException {

    private final HttpStatusCode statusCode;

    public GeocodingClientException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GeocodingClientException(String message, HttpStatusCode statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
