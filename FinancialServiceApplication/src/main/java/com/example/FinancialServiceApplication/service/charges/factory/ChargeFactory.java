package com.example.FinancialServiceApplication.service.charges.factory;

import com.example.FinancialServiceApplication.exception.BusinessException;
import com.example.FinancialServiceApplication.service.charges.ChargeStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChargeFactory {

    private final Map<String, ChargeStrategy> strategyMap;

    public ChargeFactory(List<ChargeStrategy> strategies) {

        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getChargeType().trim().toUpperCase(),
                        s -> s
                ));

        //  Debug log
        System.out.println("Available Charge Strategies: " + strategyMap.keySet());
    }

    public ChargeStrategy getStrategy(String chargeType) {

        //  NULL check
        if (chargeType == null || chargeType.trim().isEmpty()) {
            throw new BusinessException("Charge type is null or empty");
        }

        String normalizedType = chargeType.trim().toUpperCase();

        ChargeStrategy strategy = strategyMap.get(normalizedType);

        //  Proper exception
        if (strategy == null) {
            throw new BusinessException(
                    "No strategy found for charge type: " + chargeType +
                            ". Available: " + strategyMap.keySet()
            );
        }

        return strategy;
    }
}