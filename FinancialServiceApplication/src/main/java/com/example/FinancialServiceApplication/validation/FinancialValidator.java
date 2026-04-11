package com.example.FinancialServiceApplication.validation;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.exception.BusinessException;
import com.example.FinancialServiceApplication.exception.ValidationException;
import org.springframework.stereotype.Component;
@Component
public class FinancialValidator {
    public void validate(LoanDetails loan) {
        if (loan == null) {
            throw new ValidationException("Loan data is null");
        }
        if (loan.getAmount() <= 0) {
            throw new ValidationException("Loan amount must be greater than zero");
        }
        if (loan.getInterestRate() <= 0) {
            throw new ValidationException("Interest rate must be valid");
        }
        if (loan.getUserId() == null) {
            throw new ValidationException("Loan must be linked to a user");
        }
    }
}
