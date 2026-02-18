package com.tgbotmap.exception;

/**
 * Thrown when the geocoding request times out.
 */
public class GeocodingTimeoutException extends GeocodingException {

    public GeocodingTimeoutException(String message) {
        super(message);
    }

    public GeocodingTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
