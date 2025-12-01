package com.warota.shorturlservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGenerationExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "Failed to generate unique short code after 10 attempts";
        ShortCodeGenerationException exception = new ShortCodeGenerationException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String message = "Failed to generate unique short code";
        Throwable cause = new RuntimeException("Underlying cause");
        ShortCodeGenerationException exception = new ShortCodeGenerationException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionIsRuntimeException() {
        ShortCodeGenerationException exception = new ShortCodeGenerationException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(ShortCodeGenerationException.class, () -> {
            throw new ShortCodeGenerationException("Test exception");
        });
    }
}
