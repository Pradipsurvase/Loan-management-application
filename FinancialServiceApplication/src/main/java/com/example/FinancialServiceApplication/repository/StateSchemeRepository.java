package com.example.FinancialServiceApplication.repository;

import com.example.FinancialServiceApplication.entity.StateSchemeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface StateSchemeRepository extends JpaRepository<StateSchemeRule, Long> {

    List<StateSchemeRule> findByState(String state);
}