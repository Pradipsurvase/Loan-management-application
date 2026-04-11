package com.example.FinancialServiceApplication.service.charges;

import com.example.FinancialServiceApplication.entity.ChargeRule;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.exception.ChargeRuleNotFoundException;
import com.example.FinancialServiceApplication.repository.ChargeRuleRepository;
import com.example.FinancialServiceApplication.service.charges.factory.ChargeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChargesService {

    private final ChargeFactory factory;
    private final ChargeRuleRepository repo;

    public ChargesService(ChargeFactory factory,
                          ChargeRuleRepository repo) {
        this.factory = factory;
        this.repo = repo;
    }

    public ChargeResult applyCharges(LoanDetails loan, String bankName) {

        log.info("Applying charges for bank={}", bankName);

        String normalizedBank = bankName.trim().toUpperCase();

        List<ChargeRule> rules =
                repo.findByBankNameAndIsActiveTrue(normalizedBank);

        if (rules.isEmpty()) {
            throw new ChargeRuleNotFoundException(normalizedBank);
        }

        double total = 0;
        Map<String, Double> breakdown = new HashMap<>();

        for (ChargeRule rule : rules) {

            if (rule.getChargeType() == null) continue;

            ChargeStrategy strategy =
                    factory.getStrategy(rule.getChargeType());

            if (strategy.isApplicable(loan, rule)) {

                double amount = strategy.calculate(loan, rule);

                breakdown.merge(
                        rule.getChargeType().toLowerCase(),
                        amount,
                        Double::sum
                );

                total += amount;
            }
        }

        log.info("Total charges calculated={}", total);

        return new ChargeResult(total, breakdown);
    }
}