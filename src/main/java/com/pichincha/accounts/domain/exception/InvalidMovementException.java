package com.pichincha.accounts.domain.exception;

public class InvalidMovementException extends RuntimeException {
    public InvalidMovementException(String message) {
        super(message);
    }
    
    public InvalidMovementException(String message, Throwable cause) {
        super(message, cause);
    }
}