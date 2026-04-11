package com.example.FinancialServiceApplication.service.scheme.factory;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchemeFactory {

    private final List<SchemeStrategy> strategies;

    public SchemeFactory(List<SchemeStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<SchemeStrategy> getApplicable(LoanDetails loan, UserDetails user) {

        System.out.println("---- Checking all schemes ----");

        return strategies.stream()
                .peek(s -> System.out.println("Invoking: " + s.getClass().getSimpleName()))
                .filter(s -> {
                    boolean result = s.isApplicable(loan, user);
                    System.out.println(s.getClass().getSimpleName() + " applicable: " + result);
                    return result;
                })
                .collect(Collectors.toList());
    }
}