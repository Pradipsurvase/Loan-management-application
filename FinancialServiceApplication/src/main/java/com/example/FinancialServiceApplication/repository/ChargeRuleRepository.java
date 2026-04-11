package com.example.FinancialServiceApplication.repository;

import com.example.FinancialServiceApplication.entity.ChargeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ChargeRuleRepository extends JpaRepository<ChargeRule, Long> {

    List<ChargeRule> findByBankNameAndIsActiveTrue(String bankName);

    Optional<ChargeRule> findByBankNameAndChargeType(String bankName, String chargeType);
}
