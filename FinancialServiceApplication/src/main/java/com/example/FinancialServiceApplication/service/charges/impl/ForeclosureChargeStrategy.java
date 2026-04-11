package com.example.FinancialServiceApplication.service.charges.impl;
import com.example.FinancialServiceApplication.entity.ChargeRule;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.service.charges.ChargeStrategy;
import org.springframework.stereotype.Component;
@Component
public class ForeclosureChargeStrategy implements ChargeStrategy {
    @Override
    public boolean isApplicable(LoanDetails loan, ChargeRule rule) {
        return loan.isForeclosed() && loan.getOutstandingAmount() > 0;
    }
    @Override
    public double calculate(LoanDetails loan, ChargeRule rule) {
        return loan.getOutstandingAmount() * rule.getValue() / 100;
    }
    @Override
    public String getChargeType() {
        return "FORECLOSURE";
    }
}