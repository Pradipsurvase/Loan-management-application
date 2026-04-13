package com.loanmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank {

    private String id;
    private String bankName;
    private String bankCode;
    private String ifscPrefix;
    private String logoUrl;
    private Boolean isActive;
    @Transient
    private List<BankInterestRate> interestRates = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}