package com.automobilesystem.automobile.Exceptions;

/**
 * ADDED: Custom exception thrown when daily appointment limit is exceeded
 * This exception is thrown when attempting to create an appointment on a date
 * that has already reached its maximum capacity of PENDING or CONFIRMED appointments
 */
public class DailyLimitExceededException extends RuntimeException {
    
    /**
     * Constructor with error message
     * @param message The error message to display to the user
     */
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
