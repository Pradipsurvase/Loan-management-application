package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.util.EmiCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PartPaymentStrategy implements RepaymentStrategy {

    private final LoanClient loanClient;
    @Value("${min.part.payment}")
    private BigDecimal minPartPayment;

    @Override
    public void apply(List<RepaymentSchedule> list,
                      Long loanId,
                      int month,
                      BigDecimal amount) {

        RepaymentSchedule current = list.stream()
                .filter(x -> x.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new InvalidAmountException("Invalid month"));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Invalid amount");
        }

        if (amount.compareTo(minPartPayment) < 0) {
            throw new InvalidAmountException("Minimum part payment is " + minPartPayment);
        }

        if (amount.compareTo(current.getBalance()) > 0) {
            throw new InvalidAmountException("Amount exceeds outstanding balance");
        }

        if (current.isPaid()) {
            throw new InvalidAmountException("Cannot apply part payment on paid EMI");
        }

        BigDecimal newBalance = current.getBalance().subtract(amount);

        LoanDto loan = loanClient.getLoan(loanId);

        int remainingMonths = list.size() - month;

        if (remainingMonths <= 0) {
            throw new InvalidAmountException("No tenure left");
        }
        BigDecimal newEmi = EmiCalculator.calculate(
                newBalance,
                loan.getInterestRate(),
                remainingMonths
        );

        BigDecimal balance = newBalance;

        BigDecimal rate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        for (RepaymentSchedule r : list) {

            if (r.getMonth() > month) {

                BigDecimal interest = balance.multiply(rate);
                BigDecimal principal = newEmi.subtract(interest);

                balance = balance.subtract(principal);

                r.setEmi(newEmi);
                r.setInterest(interest);
                r.setPrincipal(principal);
                r.setBalance(balance);
            }
        }
    }
}