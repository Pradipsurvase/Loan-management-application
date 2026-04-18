package com.example.FinancialServiceApplication.service.scheme.impl;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.SchemeRule;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.repository.SchemeRuleRepository;
import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class ScholarshipScheme implements SchemeStrategy {

    private static final Logger log = LoggerFactory.getLogger(ScholarshipScheme.class);
    private final SchemeRuleRepository repository;

    public ScholarshipScheme(SchemeRuleRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isApplicable(LoanDetails loan, UserDetails user) {

        Optional<SchemeRule> rule =
                repository.findBySchemeName("SCHOLARSHIP");

        if (rule.isEmpty()) return false;
        boolean eligible = user.getIncome() <= rule.get().getIncomeLimit();
        log.info("Scholarship applicable: {}", eligible);

        return eligible;
    }

    @Override
    public SchemeResult evaluate(LoanDetails loan, UserDetails user) {

        SchemeRule rule =
                repository.findBySchemeName("SCHOLARSHIP")
                        .orElseThrow();

        double scholarshipAmount = rule.getSubsidyAmount();
        double updatedLoan =
                loan.getAmount() - scholarshipAmount;

        if (updatedLoan < 0) updatedLoan = 0;

        log.info("Scholarship Benefit: {}", scholarshipAmount);

        return new SchemeResult(
                "SCHOLARSHIP",
                scholarshipAmount,
                updatedLoan,
                loan.getAmount()
        );
    }
}