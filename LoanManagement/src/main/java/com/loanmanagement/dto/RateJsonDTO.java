package com.loanmanagement.dto;

import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RateJsonDTO {

    private String id;
    private String bankId; // 🔥 important

    private LoanType loanType;
    private InterestType interestType;

    private BigDecimal baseRate;
    private BigDecimal domesticDiscount;
    private BigDecimal femaleDiscount;
    private BigDecimal subsidyRate;

    private BigDecimal minLoanAmount;
    private BigDecimal maxLoanAmount;

    private Integer minTenureMonths;
    private Integer maxTenureMonths;

    private Integer maxMoratoriumMonths;

    private Integer minGracePeriodMonths;
    private Integer maxGracePeriodMonths;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Boolean isActive;
}