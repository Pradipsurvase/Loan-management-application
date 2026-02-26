package com.example.educationloan.exception;

/**
 * Exception thrown when attempting to create or update a resource
 * that would result in a duplicate conflict.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
