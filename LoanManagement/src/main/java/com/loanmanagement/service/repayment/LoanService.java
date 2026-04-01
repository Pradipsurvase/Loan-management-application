package com.loanmanagement.service.repayment;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.entity.Bank;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.InterestCalculationResult;
import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.exception.LoanBusinessException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.LoanRepository;
import com.loanmanagement.enums.LoanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public interface LoanService {

    LoanApplicationResponseDTO submitApplication(LoanApplicationRequestDTO requestDTO);

    LoanApplicationResponseDTO calculateLoan(LoanApplicationRequestDTO requestDTO);
}
