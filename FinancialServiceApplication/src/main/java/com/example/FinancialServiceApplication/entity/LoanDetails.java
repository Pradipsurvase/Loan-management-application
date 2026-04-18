package com.example.FinancialServiceApplication.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanDetails {

    private Long loanId;
    private double amount;
    private double interestRate;
    private int years;
    private Long userId;
    private int studyDuration;
    private int moratoriumPeriod;
    private String studyLocation;
    private boolean hasCollateral;
    private boolean isForeclosed;
    private double outstandingAmount;
    private double emiAmount;
    private int delayDays;
    private boolean emiBounced;
    private boolean loanCancelled;
    private String loanType;
}
