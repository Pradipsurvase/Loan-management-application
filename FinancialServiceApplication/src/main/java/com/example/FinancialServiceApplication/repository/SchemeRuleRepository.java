package com.example.FinancialServiceApplication.repository;
import com.example.FinancialServiceApplication.entity.SchemeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SchemeRuleRepository extends JpaRepository<SchemeRule, Long> {

    Optional<SchemeRule> findBySchemeName(String schemeName);
}
