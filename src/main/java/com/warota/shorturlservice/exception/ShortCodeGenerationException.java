package com.warota.shorturlservice.exception;

/**
 * Exception thrown when the service fails to generate a unique short code
 * after exhausting all retry attempts due to collisions.
 */
public class ShortCodeGenerationException extends RuntimeException {
    
    public ShortCodeGenerationException(String message) {
        super(message);
    }
    
    public ShortCodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
