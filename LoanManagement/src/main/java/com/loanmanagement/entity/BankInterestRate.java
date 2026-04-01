package com.loanmanagement.entity;

import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_interest_rates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankInterestRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String bankId;

    @Transient
    private Bank bank;

    // ── Relation ────────────────────────────────
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bank_id", nullable = false)
//    private Bank bank;


    // ── Rate Scope ───────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestType interestType;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(precision = 4, scale = 2)
    private BigDecimal floatingSpread;

    private String benchmarkName;

    @Column(precision = 4, scale = 2)
    private BigDecimal domesticDiscount;

    @Column(precision = 4, scale = 2)
    private BigDecimal femaleDiscount;

    @Column(precision = 5, scale = 2)
    private BigDecimal subsidyRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minLoanAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal maxLoanAmount;

    @Column(nullable = false)
    private Integer minTenureMonths;

    @Column(nullable = false)
    private Integer maxTenureMonths;

    @Column(nullable = false)
    private Integer maxMoratoriumMonths;

    @Column(nullable = false)
    private Integer minGracePeriodMonths;

    @Column(nullable = false)
    private Integer maxGracePeriodMonths;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Column(nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}