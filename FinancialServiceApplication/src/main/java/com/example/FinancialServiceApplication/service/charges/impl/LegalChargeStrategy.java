package com.example.FinancialServiceApplication.service.charges.impl;

import com.example.FinancialServiceApplication.entity.ChargeRule;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.service.charges.ChargeStrategy;
import org.springframework.stereotype.Component;

@Component
public class LegalChargeStrategy implements ChargeStrategy {

    @Override
    public boolean isApplicable(LoanDetails loan, ChargeRule rule) {

        return true;
    }

    /*@Override
    public boolean isApplicable(LoanDetails loan, ChargeRule rule) {
        return loan.isHasCollateral() || loan.getAmount() > 200000;
    }
    */




    @Override
    public double calculate(LoanDetails loan, ChargeRule rule) {

        return rule.getValue();
    }

    @Override
    public String getChargeType() {
        return "LEGAL";
    }
}