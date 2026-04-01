package com.loanmanagement.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanStatus;
import com.loanmanagement.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonPropertyOrder({
//        "id", "applicationNumber", "bankId", "bankName", "bankInterestRateId",
//        "applicantName", "gender", "loanType", "interestType",
//        "courseFee", "livingExpenses", "travelExpenses", "bookEquipmentCost",
//        "insuranceCoverage", "laptopAllowance", "loanAmount",
//        "baseInterestRate", "floatingSpread", "benchmarkName", "appliedInterestRate",
//        "studyPeriodMonths", "gracePeriodMonths", "moratoriumPeriod", "moratoriumInterest",
//        "principalAfterMoratorium", "totalRepayableAmount",
//        "subsidized", "subsidyScheme", "status", "createdAt", "updatedAt"
//})
public class LoanApplicationResponseDTO {

    String id;
    String applicationNumber;

    // Bank info
    String bankId;
    String bankName;
    String bankInterestRateId;

    // Applicant
    String applicantName;
    Gender gender;
    LoanType loanType;
    InterestType interestType;

    // Coverage breakdown — shown in application summary screen
    BigDecimal courseFee;
    BigDecimal livingExpenses;
    BigDecimal travelExpenses;
    BigDecimal bookEquipmentCost;
    BigDecimal insuranceCoverage;
    BigDecimal laptopAllowance;
    BigDecimal loanAmount;

    // Interest summary — shown in cost-of-loan screen
    BigDecimal baseInterestRate;
    BigDecimal floatingSpread;
    String benchmarkName;
    BigDecimal appliedInterestRate;
    Integer studyPeriodMonths;
    Integer gracePeriodMonths;
    Integer moratoriumPeriod;
    BigDecimal moratoriumInterest;
    BigDecimal principalAfterMoratorium;
    BigDecimal totalRepayableAmount;

    // Subsidy
    Boolean subsidized;
    String subsidyScheme;

    // Lifecycle
    LoanStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;



}
