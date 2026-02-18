package com.tgbotmap.exception;

/**
 * Thrown when the geocoding service returns a 5xx server error.
 */
public class GeocodingServerException extends GeocodingException {

    public GeocodingServerException(String message) {
        super(message);
    }

    public GeocodingServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
