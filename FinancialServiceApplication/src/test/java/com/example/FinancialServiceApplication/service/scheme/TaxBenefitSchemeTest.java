package com.example.FinancialServiceApplication.service.scheme;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.service.scheme.impl.TaxBenefitScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxBenefitSchemeTest {

    @Test
    void testApplicable() {
        TaxBenefitScheme scheme = new TaxBenefitScheme();

        LoanDetails loan = LoanDetails.builder()
                .amount(500000)
                .interestRate(10)
                .loanType("EDUCATION")
                .studyDuration(24)
                .build();

        UserDetails user = new UserDetails(
                1L,
                500000,
                "GENERAL",
                "MH"
        );

        assertTrue(scheme.isApplicable(loan, user));
    }

    @Test
    void testNotApplicable() {
        TaxBenefitScheme scheme = new TaxBenefitScheme();

        LoanDetails loan = LoanDetails.builder()
                .amount(500000)
                .interestRate(0)
                .loanType("EDUCATION")
                .build();

        UserDetails user = new UserDetails(1L, 500000, "GENERAL", "MH");

        assertFalse(scheme.isApplicable(loan, user));
    }
}