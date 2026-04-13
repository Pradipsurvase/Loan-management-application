package com.loanmanagement.entity;

import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankInterestRate {

    private String id;
    private String bankId;
    @Transient
    private Bank bank;
    private LoanType loanType;
    private InterestType interestType;
    private BigDecimal baseRate;
    private BigDecimal floatingSpread;
    private String benchmarkName;
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
    private LocalDateTime createdAt;

}