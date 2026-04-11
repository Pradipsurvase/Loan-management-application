package com.example.FinancialServiceApplication.service.scheme.impl;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Service;

@Service
public class AmbedkarScheme implements SchemeStrategy {

    @Override
    public boolean isApplicable(LoanDetails loan, UserDetails user) {

        boolean isCategoryEligible =
                "SC".equalsIgnoreCase(user.getCategory()) ||
                        "OBC".equalsIgnoreCase(user.getCategory());

        boolean isIncomeEligible =
                user.getIncome() <= 800000;

        boolean isAbroad =
                "ABROAD".equalsIgnoreCase(loan.getStudyLocation());

        boolean validLoan =
                loan.getAmount() > 0 &&
                        loan.getInterestRate() > 0 &&
                        loan.getStudyDuration() > 0;

        boolean eligible =
                isCategoryEligible &&
                        isIncomeEligible &&
                        isAbroad &&
                        validLoan;

        System.out.println("Ambedkar applicable: " + eligible +
                " | Income: " + user.getIncome() +
                " | Category: " + user.getCategory() +
                " | Abroad: " + loan.getStudyLocation());

        return eligible;
    }

    @Override
    public SchemeResult evaluate(LoanDetails loan, UserDetails user) {

        double monthlyRate = loan.getInterestRate() / 12.0;

        int totalMonths =
                loan.getStudyDuration() + loan.getMoratoriumPeriod();

        double interest =
                loan.getAmount() * monthlyRate * totalMonths;

        System.out.println("Ambedkar Calculation -> " +
                "Amount: " + loan.getAmount() +
                ", Rate: " + loan.getInterestRate() +
                ", Months: " + totalMonths +
                ", Benefit: " + interest);

        return new SchemeResult(
                "AMBEDKAR",
                interest,
                loan.getAmount(),
                loan.getAmount()
        );
    }
}