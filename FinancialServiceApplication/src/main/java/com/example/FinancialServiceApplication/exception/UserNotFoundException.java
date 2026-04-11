package com.example.FinancialServiceApplication.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}