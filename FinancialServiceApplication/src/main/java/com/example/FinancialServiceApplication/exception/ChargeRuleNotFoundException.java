package com.example.FinancialServiceApplication.exception;

public class ChargeRuleNotFoundException extends BusinessException {
    public ChargeRuleNotFoundException(String bank) {
        super("No charge rules found for bank: " + bank);
    }
}
