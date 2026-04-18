package com.example.FinancialServiceApplication.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "charge_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "charge_name")
    private String chargeType;

    @Column(name = "value")
    private double value;

    @Column(name = "value_type")
    private String valueType;

    @Column(name = "flat_fee")
    private Double flatFee;

    @Column(name = "is_active")
    private boolean isActive;
}