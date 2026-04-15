package com.loanmanagement.service;

import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.InterestCalculationResult;
import com.loanmanagement.entity.MarketRate;
import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import com.loanmanagement.service.repayment.InterestCalculationServiceImpl;
import com.loanmanagement.service.repayment.MockDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestCalculationServiceImplTest {

    @Mock
    private MockDataStore mockDataStore;

    @InjectMocks
    private InterestCalculationServiceImpl interestService;

    private BankInterestRate fixedRate;
    private BankInterestRate floatingRate;

    @BeforeEach
    void setUp() {
        fixedRate = new BankInterestRate();
        fixedRate.setInterestType(InterestType.FIXED);
        fixedRate.setBaseRate(new BigDecimal("10.00"));
        fixedRate.setLoanType(LoanType.DOMESTIC);
        fixedRate.setDomesticDiscount(new BigDecimal("0.50"));
        fixedRate.setFemaleDiscount(new BigDecimal("0.50"));

        floatingRate = new BankInterestRate();
        floatingRate.setInterestType(InterestType.FLOATING);
        floatingRate.setBenchmarkName("LIBOR");
        floatingRate.setFloatingSpread(new BigDecimal("2.00"));
        floatingRate.setLoanType(LoanType.INTERNATIONAL);
    }

    @Test
    @DisplayName("Calculate - Full Success path with Moratorium and Tenure")
    void calculate_Success() {
        BigDecimal principal = new BigDecimal("10000");
        InterestCalculationResult result = interestService.calculate(
                principal, fixedRate, Gender.MALE, 12, 12, 12);

        assertNotNull(result);
        assertEquals(new BigDecimal("9.50"), result.appliedInterestRate());
        assertEquals(24, result.moratoriumPeriod());
        assertEquals(new BigDecimal("1900.00"), result.moratoriumInterest());
    }

    @Test
    @DisplayName("Applied Rate - Floating Rate Logic")
    void calculateAppliedRate_Floating() {
        MarketRate market = new MarketRate();
        market.setCurrentRate(new BigDecimal("5.00"));
        when(mockDataStore.getActiveMarketRate("LIBOR")).thenReturn(market);

        BigDecimal rate = interestService.calculateAppliedRate(floatingRate, Gender.MALE);

        assertEquals(new BigDecimal("7.00"), rate);
        verify(mockDataStore).getActiveMarketRate("LIBOR");
    }

    @Test
    @DisplayName("Applied Rate - Apply Female and Domestic Discounts")
    void calculateAppliedRate_WithDiscounts() {
        BigDecimal rate = interestService.calculateAppliedRate(fixedRate, Gender.FEMALE);
        assertEquals(new BigDecimal("9.00"), rate);
    }


    @Test
    @DisplayName("Applied Rate - Hit the 1.00% Floor")
    void calculateAppliedRate_FloorLimit() {
        fixedRate.setBaseRate(new BigDecimal("1.20"));
        fixedRate.setDomesticDiscount(new BigDecimal("0.50"));
        BigDecimal rate = interestService.calculateAppliedRate(fixedRate, Gender.MALE);
        assertEquals(new BigDecimal("1.00"), rate);
    }

    @Test
    @DisplayName("Applied Rate - No Discounts Applied (Overseas Male)")
    void calculateAppliedRate_NoDiscounts() {
        fixedRate.setLoanType(LoanType.INTERNATIONAL); // Domestic discount won't apply
        BigDecimal rate = interestService.calculateAppliedRate(fixedRate, Gender.MALE);

        assertEquals(new BigDecimal("10.00"), rate);
    }

    @Test
    @DisplayName("Math - Zero Principal resulting in zero interest")
    void calculate_ZeroPrincipal() {
        BigDecimal principal = BigDecimal.ZERO;
        InterestCalculationResult result = interestService.calculate(
                principal, fixedRate, Gender.MALE, 12, 6, 60);

        assertEquals(new BigDecimal("0.00"), result.moratoriumInterest());
        assertEquals(new BigDecimal("0.00"), result.principalAfterMoratorium());
    }

    @Test
    @DisplayName("Applied Rate - Missing Discount Values (Null Safety)")
    void calculateAppliedRate_NullDiscounts() {
        fixedRate.setDomesticDiscount(null);
        fixedRate.setFemaleDiscount(null);

        BigDecimal rate = interestService.calculateAppliedRate(fixedRate, Gender.FEMALE);
        assertEquals(new BigDecimal("10.00"), rate);
    }
}