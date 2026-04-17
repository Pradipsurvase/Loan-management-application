package com.loanmanagement.util;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.exception.LoanBusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanValidationUtils {

    public BigDecimal validateAndCalculateLoanAmount(
            LoanApplicationRequestDTO req,
            BankInterestRate rate) {

        validateRateLoanTypeMatch(rate, req);

        BigDecimal loanAmount = sumCoverageBuckets(req);

        validateLoanAmountRange(loanAmount, rate);
        validateTenure(req.getRequestedTenureMonths(), rate);
        validateGracePeriod(req.getGracePeriodMonths(), rate);
        validateMoratorium(req.getStudyPeriodMonths(),
                req.getGracePeriodMonths(), rate);

        return loanAmount;
    }

    public  void validateRateLoanTypeMatch(BankInterestRate rate, LoanApplicationRequestDTO req) {
        if (rate.getLoanType() != req.getLoanType()) {
            throw new LoanBusinessException( "Loan type mismatch: expected " + rate.getLoanType() +
                    ", but got " + req.getLoanType() + ".");
        }
        if (rate.getInterestType() != req.getInterestType()) {
            throw new LoanBusinessException( "Interest type mismatch: expected " + rate.getInterestType() +
                    ", but got " + req.getInterestType() + ".");
        }
    }

    public BigDecimal sumCoverageBuckets(LoanApplicationRequestDTO req) {
        BigDecimal total = BigDecimal.ZERO;

        if (req.getCourseFee() != null)
            total = total.add(req.getCourseFee());
        if (req.getLivingExpenses() != null)
            total = total.add(req.getLivingExpenses());
        if(req.getTravelExpenses() != null)
            total = total.add(req.getTravelExpenses());
        if (req.getBookEquipmentCost() != null){
            total = total.add(req.getBookEquipmentCost());
        }
        if (req.getInsuranceCoverage() != null){
            total = total.add(req.getInsuranceCoverage());
        }
        if (req.getLaptopAllowance() != null){
            total = total.add(req.getLaptopAllowance());
        }
        return total;
    }

    public  void validateLoanAmountRange(BigDecimal amount, BankInterestRate rate){
        if (amount.compareTo(rate.getMinLoanAmount()) < 0){
            throw new LoanBusinessException("Loan amount " + amount + " is below minimum allowed "
                    + rate.getMinLoanAmount() );
        }
        if (amount.compareTo(rate.getMaxLoanAmount()) > 0){
            throw new LoanBusinessException( "Loan amount " + amount + " exceeds maximum allowed "
                    + rate.getMaxLoanAmount());
        }
    }

    public  void  validateTenure(int requestedMonths, BankInterestRate rate){
        if (requestedMonths < rate.getMinTenureMonths()) {
            throw new LoanBusinessException("Tenure must be at least " + rate.getMinTenureMonths() + " months.");
        }
        if (requestedMonths > rate.getMaxTenureMonths()) {
            throw new LoanBusinessException("Tenure cannot exceed " + rate.getMaxTenureMonths() + " months.");
        }
    }

    public  void validateGracePeriod(int gracePeriodMonths, BankInterestRate rate) {
        if (gracePeriodMonths < rate.getMinGracePeriodMonths()) {
            throw new LoanBusinessException("Grace period must be at least "
                       + rate.getMinGracePeriodMonths() + " months.");
        }
        if (gracePeriodMonths > rate.getMaxGracePeriodMonths()) {
            throw new LoanBusinessException("Grace period cannot exceed "
                            + rate.getMaxGracePeriodMonths() + " months.");
        }
    }
    public  void validateMoratorium(int studyPeriodMonths, int gracePeriodMonths, BankInterestRate rate) {
        int total = gracePeriodMonths + studyPeriodMonths;
        if (total > rate.getMaxMoratoriumMonths()) {
            throw new LoanBusinessException("Total moratorium (" + total + " months) exceeds allowed limit of "
                            + rate.getMaxMoratoriumMonths() + " months.");
        }
    }
}
