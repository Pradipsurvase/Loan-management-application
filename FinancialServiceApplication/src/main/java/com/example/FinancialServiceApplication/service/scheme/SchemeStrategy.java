package com.example.FinancialServiceApplication.service.scheme;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;


public interface SchemeStrategy {

    boolean isApplicable(LoanDetails loan, UserDetails user);
    SchemeResult evaluate(LoanDetails loan, UserDetails user);
}