package com.loanmanagement.repository;

import com.loanmanagement.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, String> {

    @Query("SELECT COUNT(l) FROM LoanApplication l WHERE l.bankId = :bankId AND YEAR(l.createdAt) = :year")
    long countApplicationsForYear(String bankId, int year);
}
