package com.automobilesystem.automobile.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ADDED: Global exception handler for the application
 * This class intercepts exceptions thrown by controllers and formats them
 * into proper HTTP responses with appropriate status codes and error messages
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ADDED: Handle DailyLimitExceededException
     * This is thrown when a user tries to book an appointment on a date that has reached its limit
     * Returns HTTP 400 (Bad Request) with the error message
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error details in JSON format
     */
    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleDailyLimitExceeded(DailyLimitExceededException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Daily Limit Exceeded");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * ADDED: Handle general RuntimeException
     * This catches any other runtime exceptions that aren't handled by specific handlers
     * Returns HTTP 500 (Internal Server Error)
     * 
     * @param ex The exception that was thrown
     * @return ResponseEntity with error details in JSON format
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
