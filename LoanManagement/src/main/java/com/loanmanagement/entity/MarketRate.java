package com.loanmanagement.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MarketRate {

    private String benchmarkName;
    private BigDecimal currentRate;
    private LocalDate effectiveFrom;
    private Boolean isActive;
}
