package com.example.FinancialServiceApplication.exception;


public class SchemeNotFoundException extends BusinessException {
    public SchemeNotFoundException(String message) {
        super(message);
    }
}