package com.loanmanagement.controller;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.dto.LoanCalculationResponseDTO;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.service.repayment.LoanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping(value = "/submit", consumes = "application/json")
    public ResponseEntity<LoanApplicationResponseDTO> submitApplication(@Valid @RequestBody LoanApplicationRequestDTO request) {
        log.info("API /submit called for applicant: {}", request.getApplicantName());
        LoanApplicationResponseDTO response = loanService.submitApplication(request);
        log.info("API /submit success. ApplicationNumber: {}", response.getApplicationNumber());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate")
    public ResponseEntity<LoanCalculationResponseDTO> calculateLoan(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("API /calculate called for applicant: {}", requestDTO.getApplicantName());
        LoanCalculationResponseDTO response = loanService.calculateLoan(requestDTO);
        log.info("API /calculate completed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{applicationNumber}")
    public ResponseEntity<LoanApplicationResponseDTO> getLoanByApplicationNumber(@PathVariable String applicationNumber) {
        log.info("API GET loan called for applicationNumber: {}", applicationNumber);
        LoanApplicationResponseDTO response = loanService.getLoanByApplicationNumber(applicationNumber);
        if (response == null) {
            throw new ResourceNotFoundException("Loan not found for application number: " + applicationNumber);
        }
        log.info("Loan fetch successful for applicationNumber: {}", applicationNumber);
        return ResponseEntity.ok(response);
    }

}
