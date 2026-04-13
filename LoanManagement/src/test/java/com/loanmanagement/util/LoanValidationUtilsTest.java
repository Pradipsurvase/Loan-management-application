package com.loanmanagement.util;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import com.loanmanagement.exception.LoanBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class LoanValidationUtilsTest {

    private LoanValidationUtils utils;
    private LoanApplicationRequestDTO request;
    private BankInterestRate rate;

    @BeforeEach
    void setUp() {
        utils = new LoanValidationUtils();

        request = new LoanApplicationRequestDTO();
        request.setLoanType(LoanType.DOMESTIC);
        request.setInterestType(InterestType.FIXED);
        request.setCourseFee(new BigDecimal("5000"));
        request.setLivingExpenses(new BigDecimal("2000"));
        request.setRequestedTenureMonths(60);
        request.setStudyPeriodMonths(12);
        request.setGracePeriodMonths(6);

        rate = new BankInterestRate();
        rate.setLoanType(LoanType.DOMESTIC);
        rate.setInterestType(InterestType.FIXED);
        rate.setMinLoanAmount(new BigDecimal("1000"));
        rate.setMaxLoanAmount(new BigDecimal("50000"));
        rate.setMinTenureMonths(12);
        rate.setMaxTenureMonths(120);
        rate.setMinGracePeriodMonths(0);
        rate.setMaxGracePeriodMonths(12);
        rate.setMaxMoratoriumMonths(24);
    }

    @Test
    @DisplayName("Should validate and calculate total amount successfully")
    void validateAndCalculate_Success() {
        BigDecimal result = utils.validateAndCalculateLoanAmount(request, rate);
        assertEquals(new BigDecimal("7000"), result);
    }

    @Test
    @DisplayName("Sum Coverage - Should handle null fields and sum correctly")
    void sumCoverage_WithNulls() {
        request.setCourseFee(new BigDecimal("1000"));
        request.setLivingExpenses(null); // Should be skipped
        request.setTravelExpenses(new BigDecimal("500"));
        request.setBookEquipmentCost(new BigDecimal("200"));
        request.setInsuranceCoverage(new BigDecimal("300"));
        request.setLaptopAllowance(new BigDecimal("1000"));

        BigDecimal total = utils.sumCoverageBuckets(request);
        assertEquals(new BigDecimal("3000"), total);
    }

    @Test
    @DisplayName("Type Match - Should throw exception on mismatched Loan Type")
    void validateTypeMatch_LoanTypeMismatch() {
        rate.setLoanType(LoanType.INTERNATIONAL);
        assertThrows(LoanBusinessException.class, () -> utils.validateRateLoanTypeMatch(rate, request));
    }

    @Test
    @DisplayName("Type Match - Should throw exception on mismatched Interest Type")
    void validateTypeMatch_InterestTypeMismatch() {
        rate.setInterestType(InterestType.FLOATING);
        assertThrows(LoanBusinessException.class, () -> utils.validateRateLoanTypeMatch(rate, request));
    }

    @Test
    @DisplayName("Loan Range - Should throw exception if below minimum")
    void validateRange_BelowMin() {
        BigDecimal lowAmount = new BigDecimal("500");
        assertThrows(LoanBusinessException.class, () -> utils.validateLoanAmountRange(lowAmount, rate));
    }

    @Test
    @DisplayName("Loan Range - Should throw exception if above maximum")
    void validateRange_AboveMax() {
        BigDecimal highAmount = new BigDecimal("100000");
        assertThrows(LoanBusinessException.class, () -> utils.validateLoanAmountRange(highAmount, rate));
    }

    @Test
    @DisplayName("Tenure - Should throw exception if below minimum")
    void validateTenure_TooShort() {
        assertThrows(LoanBusinessException.class, () -> utils.validateTenure(6, rate));
    }

    @Test
    @DisplayName("Tenure - Should throw exception if above maximum")
    void validateTenure_TooLong() {
        assertThrows(LoanBusinessException.class, () -> utils.validateTenure(200, rate));
    }

    @Test
    @DisplayName("Grace Period - Should throw exception if out of bounds")
    void validateGrace_Invalid() {
        assertThrows(LoanBusinessException.class, () -> utils.validateGracePeriod(-1, rate));
        assertThrows(LoanBusinessException.class, () -> utils.validateGracePeriod(24, rate));
    }

    @Test
    @DisplayName("Moratorium - Should throw exception if combined study+grace exceeds max")
    void validateMoratorium_ExceedsMax() {
        // Study 20 + Grace 10 = 30 (Rate max is 24)
        assertThrows(LoanBusinessException.class, () -> utils.validateMoratorium(20, 10, rate));
    }
}