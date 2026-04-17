package com.loanmanagement.service.repayment;

import com.loanmanagement.constants.LoanConstants;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.InterestCalculationResult;
import com.loanmanagement.entity.MarketRate;
import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InterestCalculationServiceImpl implements InterestCalculationService{

    private static final Logger log = LoggerFactory.getLogger(InterestCalculationServiceImpl.class);

    private final MockDataStore mockDataStore;

    public InterestCalculationServiceImpl(MockDataStore mockDataStore) {
        this.mockDataStore = mockDataStore;
    }

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public InterestCalculationResult calculate(BigDecimal principal, BankInterestRate rate, Gender gender, int studyPeriodMonths, int gracePeriodMonths, int requestedTenureMonths) {
        log.debug("Interest calculation started");

        BigDecimal appliedRate = calculateAppliedRate(rate, gender);
        log.info("Applied interest rate: {}", appliedRate);

        int moratoriumPeriod = studyPeriodMonths + gracePeriodMonths;
        log.info("Moratorium period (months): {}", moratoriumPeriod);

        BigDecimal moratoriumInterest = calculateMoratoriumInterest(principal, appliedRate, moratoriumPeriod);
        log.info("Moratorium interest: {}", moratoriumInterest);

        BigDecimal principalAfterMoratorium = principal.add(moratoriumInterest);
        BigDecimal totalRepayableAmount = calculateTotalRepayable(principalAfterMoratorium, appliedRate, requestedTenureMonths);
        log.info("Total repayable amount: {}", totalRepayableAmount);

        log.debug("Interest calculation completed");
        return new InterestCalculationResult(
                appliedRate,
                moratoriumInterest,
                principalAfterMoratorium,
                totalRepayableAmount,
                moratoriumPeriod
        );
    }

    @Override
    public BigDecimal calculateAppliedRate(BankInterestRate rate, Gender gender) {
        BigDecimal appliedRate;

        if (rate.getInterestType() == InterestType.FIXED) {
            appliedRate = rate.getBaseRate();
        } else {
            MarketRate market = mockDataStore.getActiveMarketRate(rate.getBenchmarkName());

            appliedRate = market.getCurrentRate().add(rate.getFloatingSpread());
        }
        if (rate.getLoanType() == LoanType.DOMESTIC && rate.getDomesticDiscount() != null) {
            appliedRate = appliedRate.subtract(rate.getDomesticDiscount());
        }
        if (Gender.FEMALE.equals(gender) && rate.getFemaleDiscount() != null) {
            appliedRate = appliedRate.subtract(rate.getFemaleDiscount());
        }
        if (appliedRate.compareTo(LoanConstants.MIN_INTEREST_RATE) < 0) {
            appliedRate = LoanConstants.MIN_INTEREST_RATE;
        }
        return appliedRate.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public BigDecimal calculateMoratoriumInterest(BigDecimal principal, BigDecimal appliedRate, int moratoriumPeriod) {
        BigDecimal rateDecimal = appliedRate.divide(BigDecimal.valueOf(100), 10, ROUNDING_MODE);
        BigDecimal years = BigDecimal.valueOf(moratoriumPeriod)
                .divide(BigDecimal.valueOf(12), 10, ROUNDING_MODE);
        return principal.multiply(rateDecimal).multiply(years).setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public BigDecimal calculateTotalRepayable(BigDecimal principalAfterMoratorium, BigDecimal appliedRate, int tenureMonths) {
        BigDecimal rateDecimal = appliedRate.divide(BigDecimal.valueOf(100), 10, ROUNDING_MODE);
        BigDecimal years = BigDecimal.valueOf(tenureMonths)
                .divide(BigDecimal.valueOf(12), 10, ROUNDING_MODE);
        BigDecimal interest = principalAfterMoratorium.multiply(rateDecimal).multiply(years);
        return principalAfterMoratorium.add(interest).setScale(SCALE, ROUNDING_MODE);
    }
}
