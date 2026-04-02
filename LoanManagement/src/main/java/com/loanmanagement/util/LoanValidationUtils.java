package com.loanmanagement.util;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.exception.LoanBusinessException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

public class LoanValidationUtils {

    public static void validateRateLoanTypeMatch(BankInterestRate rate, LoanApplicationRequestDTO req) {
        if (rate.getLoanType() != req.getLoanType()) {
            throw new LoanBusinessException("Selected rate is for " + rate.getLoanType() +
                                            " loans but request specifies " + req.getLoanType() + " loan.");
        }
        if (rate.getInterestType() != req.getInterestType()) {
            throw new LoanBusinessException("Selected rate is " + rate.getInterestType() +
                                             " but request specifies " + req.getInterestType() + ".");
        }
    }

    public static BigDecimal sumCoverageBuckets(LoanApplicationRequestDTO req) {
        return Stream.of(
                        req.getCourseFee(),
                        req.getLivingExpenses(),
                        req.getTravelExpenses(),
                        req.getBookEquipmentCost(),
                        req.getInsuranceCoverage(),
                        req.getLaptopAllowance()
                )
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static void validateLoanAmountRange(BigDecimal amount, BankInterestRate rate){
        if (amount.compareTo(rate.getMinLoanAmount()) < 0){
            throw new LoanBusinessException("Total loan amount " + amount + " is below the minimum of "
                    + rate.getMinLoanAmount() + " for this bank and loan type.");
        }
        if (amount.compareTo(rate.getMaxLoanAmount()) > 0){
            throw new LoanBusinessException("Total loan amount " + amount + " exceeds the maximum of " +
                    rate.getMaxLoanAmount() + "for this bank and loan type.");
        }
    }

    public static void  validateTenure(int requestedMonths, BankInterestRate rate){
        if (requestedMonths < rate.getMinTenureMonths()) {
            throw new LoanBusinessException(
                    "Requested tenure " + requestedMonths + " months is below the minimum of " +
                            rate.getMinTenureMonths() + " months for this bank.");
        }
        if (requestedMonths > rate.getMaxTenureMonths()) {
            throw new LoanBusinessException(
                    "Requested tenure " + requestedMonths + " months exceeds the maximum of " +
                            rate.getMaxTenureMonths() + " months for this bank.");
        }
    }

    public static void validateGracePeriod(int gracePeriodMonths, BankInterestRate rate) {
        if (gracePeriodMonths < rate.getMinGracePeriodMonths()) {
            throw new LoanBusinessException(
                    "Grace period " + gracePeriodMonths + " months is below bank minimum of " +
                            rate.getMinGracePeriodMonths() + " months.");
        }
        if (gracePeriodMonths > rate.getMaxGracePeriodMonths()) {
            throw new LoanBusinessException(
                    "Grace period " + gracePeriodMonths + " months exceeds bank maximum of " +
                            rate.getMaxGracePeriodMonths() + " months.");
        }
    }
    public static void validateMoratorium(int studyPeriodMonths, int gracePeriodMonths, BankInterestRate rate) {
        int total = gracePeriodMonths + studyPeriodMonths;
        if (total > rate.getMaxMoratoriumMonths()) {
            throw new LoanBusinessException(
                    "Combined moratorium period (study " + studyPeriodMonths + " + grace " + gracePeriodMonths +
                            " = " + total + " months) exceeds bank maximum of " +
                            rate.getMaxMoratoriumMonths() + " months.");
        }
    }
}
