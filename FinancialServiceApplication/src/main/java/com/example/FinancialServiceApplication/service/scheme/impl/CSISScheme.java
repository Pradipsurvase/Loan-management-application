package com.example.FinancialServiceApplication.service.scheme.impl;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Service;

@Service
public class CSISScheme implements SchemeStrategy {

    @Override
    public boolean isApplicable(LoanDetails loan, UserDetails user) {

        if (loan == null || user == null) {
            return false;
        }

        boolean isIncomeEligible = user.getIncome() <= 450000;

        boolean isEducationLoan =
                loan.getLoanType() != null &&
                        loan.getLoanType().equalsIgnoreCase("EDUCATION");

        boolean isIndiaStudy =
                loan.getStudyLocation() != null &&
                        loan.getStudyLocation().equalsIgnoreCase("INDIA");

        boolean eligible = isIncomeEligible && isEducationLoan && isIndiaStudy;

        System.out.println("CSIS applicable: " + eligible +
                " | Income: " + user.getIncome() +
                " | Location: " + loan.getStudyLocation());

        return eligible;
    }

    @Override
    public SchemeResult evaluate(LoanDetails loan, UserDetails user) {

        if (loan == null) {
            return new SchemeResult("CSIS", 0, 0,0);
        }

        double monthlyRate = loan.getInterestRate() / 12;

        int totalMonths =
                loan.getStudyDuration() + loan.getMoratoriumPeriod();

        double interestSubsidy =
                loan.getAmount() * monthlyRate * totalMonths;

        System.out.println("CSIS Calculation -> " +
                "Amount: " + loan.getAmount() +
                ", Rate: " + loan.getInterestRate() +
                ", Months: " + totalMonths +
                ", Subsidy: " + interestSubsidy);

        return new SchemeResult(
                "CSIS",
                interestSubsidy,
                loan.getAmount(),
                loan.getAmount()
        );
    }
}