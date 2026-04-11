package com.example.FinancialServiceApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessRequest {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Selected scheme is required")
    private String selectedScheme;
}