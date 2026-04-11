package com.example.FinancialServiceApplication.service.charges.impl;

import com.example.FinancialServiceApplication.entity.ChargeRule;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.service.charges.ChargeStrategy;
import org.springframework.stereotype.Component;

@Component
public class ProcessingFeeStrategy implements ChargeStrategy {

    @Override
    public boolean isApplicable(LoanDetails loan, ChargeRule rule) {

        return true;
    }

    @Override
    public double calculate(LoanDetails loan, ChargeRule rule) {


        return loan.getAmount() * rule.getValue() / 100;
    }

    @Override
    public String getChargeType() {
        return "PROCESSING";
    }
}