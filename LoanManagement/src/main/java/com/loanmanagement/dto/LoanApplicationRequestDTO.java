package com.loanmanagement.dto;

import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationRequestDTO {

    @NotBlank(message = "bankId is required")
    private String bankId;

    @NotBlank(message = "bankInterestRateId is required")
    private String bankInterestRateId;

    @NotBlank(message = "applicantName is required")
    @Size(min = 2, max = 100, message = "applicantName must be between 2 and 100 characters")
    private String applicantName;

    @NotNull(message = "gender is required")
    private Gender gender;

    @NotNull(message = "loanType is required")
    private LoanType loanType;

    @NotNull(message = "interestType is required")
    private InterestType interestType;

    @DecimalMin(value = "0.0", inclusive = false, message = "courseFee must be greater than 0")
    private BigDecimal courseFee;

    @DecimalMin(value = "0.0", message = "livingExpenses cannot be negative")
    private BigDecimal livingExpenses;

    @DecimalMin(value = "0.0", message = "travelExpenses cannot be negative")
    private BigDecimal travelExpenses;

    @DecimalMin(value = "0.0", message = "bookEquipmentCost cannot be negative")
    private BigDecimal bookEquipmentCost;

    @DecimalMin(value = "0.0", message = "insuranceCoverage cannot be negative")
    private BigDecimal insuranceCoverage;

    @DecimalMin(value = "0.0", message = "laptopAllowance cannot be negative")
    private BigDecimal laptopAllowance;

    @NotNull(message = "studyPeriodMonths is required")
    @Min(value = 1,  message = "studyPeriodMonths must be at least 1 month")
    @Max(value = 84, message = "studyPeriodMonths cannot exceed 84 months (7 years)")
    private Integer studyPeriodMonths;

    @NotNull(message = "gracePeriodMonths is required")
    @Min(value = 0,  message = "gracePeriodMonths cannot be negative")
    @Max(value = 12, message = "gracePeriodMonths cannot exceed 12 months")
    private Integer gracePeriodMonths;

    @NotNull(message = "requestedTenureMonths is required")
    @Min(value = 12,  message = "requestedTenureMonths must be at least 12 months")
    @Max(value = 180, message = "requestedTenureMonths cannot exceed 180 months (15 years)")
    private Integer requestedTenureMonths;

    private Boolean subsidized;

    @Size(max = 50, message = "subsidyScheme name cannot exceed 50 characters")
    private String subsidyScheme;

}
