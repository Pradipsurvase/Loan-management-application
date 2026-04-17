package com.loanmanagement.service.repayment;

import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.InterestCalculationResult;
import com.loanmanagement.enums.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


public interface InterestCalculationService {
    InterestCalculationResult calculate(
            BigDecimal principal,
            BankInterestRate rate,
            Gender gender,
            int studyPeriodMonths,
            int gracePeriodMonths,
            int requestedTenureMonths);

    BigDecimal calculateAppliedRate(BankInterestRate rate, Gender gender);

    BigDecimal calculateMoratoriumInterest(BigDecimal principal, BigDecimal appliedRate, int moratoriumPeriod);

    BigDecimal calculateTotalRepayable(BigDecimal principalAfterMoratorium, BigDecimal appliedRate, int tenureMonths);



}
