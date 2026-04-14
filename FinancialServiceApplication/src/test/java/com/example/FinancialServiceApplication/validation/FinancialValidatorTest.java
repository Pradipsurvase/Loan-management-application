package com.example.FinancialServiceApplication.validation;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FinancialValidatorTest {

    private final FinancialValidator validator = new FinancialValidator();

    @Test
    void testValidLoan() {
        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .userId(10L)
                .amount(100000)
                .interestRate(10)
                .build();

        assertDoesNotThrow(() -> validator.validate(loan));
    }

    @Test
    void testInvalidLoan() {
        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .userId(null)
                .amount(0)
                .interestRate(0)
                .build();

        assertThrows(Exception.class, () -> validator.validate(loan));
    }
}