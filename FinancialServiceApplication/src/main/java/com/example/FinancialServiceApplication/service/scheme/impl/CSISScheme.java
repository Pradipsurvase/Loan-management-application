package com.example.FinancialServiceApplication.service.scheme.impl;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CSISScheme implements SchemeStrategy {

    private static final Logger log = LoggerFactory.getLogger(CSISScheme.class);

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

        log.info("CSIS applicable: {} | Income: {} | Location: {}",
                eligible,
                user.getIncome(),
                loan.getStudyLocation());

        return eligible;
    }

    @Override
    public SchemeResult evaluate(LoanDetails loan, UserDetails user) {
        if (loan == null) {
            return new SchemeResult("CSIS", 0, 0, 0);
        }

        double monthlyRate = loan.getInterestRate() / 12;

        int totalMonths =
                loan.getStudyDuration() + loan.getMoratoriumPeriod();

        double interestSubsidy =
                loan.getAmount() * monthlyRate * totalMonths;

        log.info("CSIS Calculation -> Amount: {}, Rate: {}, Months: {}, Subsidy: {}",
                loan.getAmount(),
                loan.getInterestRate(),
                totalMonths,
                interestSubsidy);

        return new SchemeResult(
                "CSIS",
                interestSubsidy,
                loan.getAmount(),
                loan.getAmount()
        );
    }
}