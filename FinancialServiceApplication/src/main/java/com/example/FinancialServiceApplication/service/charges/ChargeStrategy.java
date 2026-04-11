package com.example.FinancialServiceApplication.service.charges;

import com.example.FinancialServiceApplication.entity.ChargeRule;
import com.example.FinancialServiceApplication.entity.LoanDetails;

public interface ChargeStrategy {


        boolean isApplicable(LoanDetails loan, ChargeRule rule);

        double calculate(LoanDetails loan, ChargeRule rule);

        String getChargeType();
    }


