package com.example.FinancialServiceApplication.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "state_scheme")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StateSchemeRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;
    private String schemeName;
    private double minLoan;
    private double maxLoan;
    private double maxIncome;
    private String category;
    private double benefitPercentage;
}