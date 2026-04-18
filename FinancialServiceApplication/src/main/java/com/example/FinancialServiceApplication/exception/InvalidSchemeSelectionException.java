package com.example.FinancialServiceApplication.exception;


public class InvalidSchemeSelectionException extends BusinessException {
    public InvalidSchemeSelectionException(String scheme) {
        super("Invalid or not eligible scheme selected: " + scheme);
    }
}
