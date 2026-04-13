package com.example.FinancialServiceApplication.exception;

public class LoanNotFoundException extends BusinessException {
    public LoanNotFoundException(Long loanId) {
        super("Loan not found with id: " + loanId);
    }
}
