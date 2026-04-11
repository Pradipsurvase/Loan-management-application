package com.example.FinancialServiceApplication.service.charges.impl;
import com.example.FinancialServiceApplication.entity.ChargeRule;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.service.charges.ChargeStrategy;
import org.springframework.stereotype.Component;
@Component
public class LatePaymentChargeStrategy implements ChargeStrategy {
    @Override
    public boolean isApplicable(LoanDetails loan, ChargeRule rule) {
        return loan.getDelayDays() > 0 && loan.getEmiAmount() > 0;
    }
    @Override
    public double calculate(LoanDetails loan, ChargeRule rule) {
        double percentagePart = loan.getEmiAmount() * rule.getValue() / 100;
        double flat = rule.getFlatFee() != null ? rule.getFlatFee() : 0;
        return percentagePart + flat;
    }
    @Override
    public String getChargeType() {
        return "LATE_PAYMENT";
    }
}