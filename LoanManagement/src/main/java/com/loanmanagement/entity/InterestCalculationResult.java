package com.loanmanagement.entity;

import java.math.BigDecimal;

public record InterestCalculationResult(
        BigDecimal appliedInterestRate,
        BigDecimal moratoriumInterest,
        BigDecimal principalAfterMoratorium,
        BigDecimal totalRepayableAmount,
        int moratoriumPeriod
) {}