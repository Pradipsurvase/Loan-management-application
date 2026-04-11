package com.example.FinancialServiceApplication.service.scheme.impl;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Service;
@Service
public class TaxBenefitScheme implements SchemeStrategy {
    @Override
    public boolean isApplicable(LoanDetails loan, UserDetails user) {
        boolean eligible =
                loan.getAmount() > 0 &&
                        loan.getInterestRate() > 0 &&
                        user.getIncome() > 0;
        System.out.println("80E applicable: " + eligible);
        return eligible;
    }
    @Override
    public SchemeResult evaluate(LoanDetails loan, UserDetails user) {
        double monthlyRate = loan.getInterestRate() / 12;
        double annualInterest =
                loan.getAmount() * monthlyRate * 12;
        double taxRate = getTaxRate(user.getIncome());
        double taxSaved = annualInterest * taxRate;
        System.out.println("Tax Benefit: " + taxSaved);
        return new SchemeResult(
                "SECTION_80E",
                taxSaved,
                loan.getAmount(),
                loan.getAmount()
        );
    }
    private double getTaxRate(double income) {
        if (income <= 500000) return 0.05;
        else if (income <= 1000000) return 0.20;
        else return 0.30;
    }
}