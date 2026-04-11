package com.example.FinancialServiceApplication.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class FinancialResponse {
    private double originalLoan;
    private double updatedLoan;
    private double subsidy;
    private double charges;
    private double finalPayable;
}
