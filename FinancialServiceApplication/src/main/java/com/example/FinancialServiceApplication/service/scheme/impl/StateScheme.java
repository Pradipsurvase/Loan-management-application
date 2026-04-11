package com.example.FinancialServiceApplication.service.scheme.impl;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.StateSchemeRule;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.repository.StateSchemeRepository;
import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StateScheme implements SchemeStrategy {

    private final StateSchemeRepository repository;

    public StateScheme(StateSchemeRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isApplicable(LoanDetails loan, UserDetails user) {

        if (loan == null || user == null) return false;

        List<StateSchemeRule> rules = repository.findByState(user.getState());

        if (rules == null || rules.isEmpty()) return false;

        boolean eligible = rules.stream().anyMatch(rule ->
                loan.getAmount() >= rule.getMinLoan() &&
                        loan.getAmount() <= rule.getMaxLoan() &&
                        user.getIncome() <= rule.getMaxIncome() &&
                        ("ANY".equalsIgnoreCase(rule.getCategory()) ||
                                rule.getCategory().equalsIgnoreCase(user.getCategory()))
        );

        System.out.println("StateScheme applicable: " + eligible +
                " | State: " + user.getState());

        return eligible;
    }

    @Override
    public SchemeResult evaluate(LoanDetails loan, UserDetails user) {

        List<StateSchemeRule> rules = repository.findByState(user.getState());

        double maxBenefit = 0;

        for (StateSchemeRule rule : rules) {

            boolean eligible =
                    loan.getAmount() >= rule.getMinLoan() &&
                            loan.getAmount() <= rule.getMaxLoan() &&
                            user.getIncome() <= rule.getMaxIncome() &&
                            ("ANY".equalsIgnoreCase(rule.getCategory()) ||
                                    rule.getCategory().equalsIgnoreCase(user.getCategory()));

            if (eligible) {

                double benefit =
                        loan.getAmount() * rule.getBenefitPercentage() / 100.0;

                System.out.println("StateScheme rule matched -> %: "
                        + rule.getBenefitPercentage()
                        + " Benefit: " + benefit);

                maxBenefit = Math.max(maxBenefit, benefit);
            }
        }

        return new SchemeResult(
                "STATE_SCHEME",
                maxBenefit,
                loan.getAmount(),
                loan.getAmount()
        );
    }
}