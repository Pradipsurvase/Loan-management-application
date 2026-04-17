package com.loanmanagement.dto;


import com.loanmanagement.enums.LoanStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationResponseDTO {

    private String loanId;
    private String applicationNumber;
    private String status;

    private Applicant applicant;
    private Bank bank;
    private LoanDetails loanDetails;
    private Expenses expenses;
    private Repayment repayment;
    private Subsidy subsidy;
    private Timestamps timestamps;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Applicant {
        private String name;
        private String gender;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bank {
        private String id;
        private String name;
        private String interestRateId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoanDetails {
        private String loanType;
        private String interestType;
        private BigDecimal baseInterestRate;
        private BigDecimal appliedInterestRate;
        private BigDecimal loanAmount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Expenses {
        private BigDecimal courseFee;
        private BigDecimal livingExpenses;
        private BigDecimal travelExpenses;
        private BigDecimal bookEquipmentCost;
        private BigDecimal insuranceCoverage;
        private BigDecimal laptopAllowance;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Repayment {
        private Integer studyPeriodMonths;
        private Integer gracePeriodMonths;
        private Integer moratoriumPeriodMonths;
        private BigDecimal moratoriumInterest;
        private BigDecimal principalAfterMoratorium;
        private BigDecimal totalRepayableAmount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Subsidy {
        private Boolean isSubsidized;
        private String scheme;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Timestamps {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

