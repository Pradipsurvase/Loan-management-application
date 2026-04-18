package com.example.FinancialServiceApplication.service.charges.factory;

import com.example.FinancialServiceApplication.exception.BusinessException;
import com.example.FinancialServiceApplication.service.charges.ChargeStrategy;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChargeFactory {

    private static final Logger log = LoggerFactory.getLogger(ChargeFactory.class);

    private final Map<String, ChargeStrategy> strategyMap;

    public ChargeFactory(List<ChargeStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getChargeType().trim().toUpperCase(),
                        s -> s
                ));

        log.info("Available Charge Strategies: {}", strategyMap.keySet());
    }

    public ChargeStrategy getStrategy(String chargeType) {
        if (chargeType == null || chargeType.trim().isEmpty()) {
            throw new BusinessException("Charge type is null or empty");
        }

        String normalizedType = chargeType.trim().toUpperCase();
        ChargeStrategy strategy = strategyMap.get(normalizedType);

        if (strategy == null) {
            throw new BusinessException(
                    "No strategy found for charge type: " + chargeType +
                            ". Available: " + strategyMap.keySet()
            );
        }

        return strategy;
    }
}