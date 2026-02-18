package com.tgbotmap.exception;

/**
 * Base exception for all geocoding-related errors.
 */
public class GeocodingException extends RuntimeException {

    public GeocodingException(String message) {
        super(message);
    }

    public GeocodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
