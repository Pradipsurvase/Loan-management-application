package com.loanmanagement.repository;

import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, String> {

    Optional<LoanApplication> findByApplicationNumber(String applicationNumber);

    boolean existsByApplicantNameAndBankId(String applicantName, String bankId);
}
