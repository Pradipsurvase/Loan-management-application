package com.example.FinancialServiceApplication.service.scheme.factory;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.SchemeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SchemeFactory {

    private final List<SchemeStrategy> strategies;

    public SchemeFactory(List<SchemeStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<SchemeStrategy> getApplicable(LoanDetails loan, UserDetails user) {

        log.info("---- Checking all schemes ----");

        return strategies.stream()
                .peek(s -> log.info("Invoking: {}", s.getClass().getSimpleName()))
                .filter(s -> {
                    boolean result = s.isApplicable(loan, user);
                    log.info("{} applicable: {}", s.getClass().getSimpleName(), result);
                    return result;
                })
                .collect(Collectors.toList());
    }
}