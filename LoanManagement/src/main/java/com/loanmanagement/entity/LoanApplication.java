package com.loanmanagement.entity;

import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanStatus;
import com.loanmanagement.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String applicationNumber;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bank_id", nullable = false)
//    private Bank bank;

    @Column(name = "bank_id")
    private String bankId;

    @Column(name = "bank_name")
    private String bankName;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bank_interest_rate_id", nullable = false)
//    private BankInterestRate bankInterestRate;


    @Column(name = "bank_interest_rate_id")
    private String bankInterestRateId;

    @Column(nullable = false)
    private String applicantName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestType interestType;

    @Column(precision = 15, scale = 2)
    private BigDecimal courseFee;

    @Column(precision = 15, scale = 2)
    private BigDecimal livingExpenses;

    @Column(precision = 15, scale = 2)
    private BigDecimal travelExpenses;

    @Column(precision = 15, scale = 2)
    private BigDecimal bookEquipmentCost;

    @Column(precision = 15, scale = 2)
    private BigDecimal insuranceCoverage;

    @Column(precision = 15, scale = 2)
    private BigDecimal laptopAllowance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal baseInterestRate;

    BigDecimal floatingSpread;

    String benchmarkName;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal appliedInterestRate;

    @Column(nullable = false)
    private Integer studyPeriodMonths;

    @Column(nullable = false)
    private Integer gracePeriodMonths;

    @Column(nullable = false)
    private Integer moratoriumPeriod;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal moratoriumInterest;
    //      formula: loanAmount × (appliedInterestRate/100) × (moratoriumPeriod/12)
    //      Example: ₹10L × 10% × 2 years = ₹2L moratorium interest

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAfterMoratorium;
    // WHY: = loanAmount + moratoriumInterest

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRepayableAmount;

    @Column(nullable = false)
    private Boolean subsidized;

    private String subsidyScheme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}