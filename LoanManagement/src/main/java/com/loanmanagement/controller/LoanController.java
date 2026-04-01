package com.loanmanagement.controller;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.service.repayment.LoanService;
import com.loanmanagement.util.LoanValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/loan")

public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/submit")
    public ResponseEntity<LoanApplicationResponseDTO> submitApplication(
            @Valid @RequestBody LoanApplicationRequestDTO request) {
        System.out.println("loan API HIT______");

        LoanApplicationResponseDTO response = loanService.submitApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/calculate")
    public ResponseEntity<LoanApplicationResponseDTO> calculateLoan(
            @Valid @RequestBody LoanApplicationRequestDTO requestDTO) {

        LoanApplicationResponseDTO response = loanService.calculateLoan(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
