package com.example.FinancialServiceApplication.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SchemeRequest {
    @NotNull(message = "Loan ID is required")
    private Long loanId;
}