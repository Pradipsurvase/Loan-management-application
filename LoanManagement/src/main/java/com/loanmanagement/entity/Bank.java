package com.loanmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "banks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String bankName;

    @Column(nullable = false, unique = true, length = 10)
    private String bankCode;
    // short code used as prefix in application number generation
    //      e.g. bankCode="SBI001" → applicationNumber="SBI001-DOM-20240001"

    @Column(length = 4)
    private String ifscPrefix;

    private String logoUrl;

    @Column(nullable = false)
    private Boolean isActive;

    @Transient
    private List<BankInterestRate> interestRates = new ArrayList<>();

//    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<BankInterestRate> interestRates;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}