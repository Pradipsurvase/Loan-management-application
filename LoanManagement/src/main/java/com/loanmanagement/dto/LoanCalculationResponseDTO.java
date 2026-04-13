package com.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCalculationResponseDTO {

    private BigDecimal loanAmount;
    private BigDecimal appliedInterestRate;

    private Integer moratoriumPeriod;
    private BigDecimal moratoriumInterest;

    private BigDecimal principalAfterMoratorium;
    private BigDecimal totalRepayableAmount;
}