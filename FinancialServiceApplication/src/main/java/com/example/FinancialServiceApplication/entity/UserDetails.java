package com.example.FinancialServiceApplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetails {
    private Long userId;
    private double income;
    private String category;
    private String state;
}

