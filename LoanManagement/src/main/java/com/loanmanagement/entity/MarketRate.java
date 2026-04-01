package com.loanmanagement.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MarketRate {

    private String benchmarkName;   // RBI_REPO
    private BigDecimal currentRate; // 6.5
    private LocalDate effectiveFrom;
    private Boolean isActive;
}
