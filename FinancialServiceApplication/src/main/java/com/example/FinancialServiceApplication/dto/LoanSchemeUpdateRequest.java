package com.example.FinancialServiceApplication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanSchemeUpdateRequest {

    private Long loanId;
    private String schemeName;
    private double subsidyAmount;
}