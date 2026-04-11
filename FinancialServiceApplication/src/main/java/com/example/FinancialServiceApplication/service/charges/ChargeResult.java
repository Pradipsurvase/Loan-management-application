package com.example.FinancialServiceApplication.service.charges;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
@Data
public class ChargeResult {
    private double totalCharges;
    private Map<String, Double> breakdown;
    public ChargeResult(double totalCharges, Map<String, Double> breakdown) {
        this.totalCharges = totalCharges;
        this.breakdown = breakdown;
    }
    public double getTotalCharges() {
        return totalCharges;
    }
    public Map<String, Double> getBreakdown() {
        return breakdown;
    }
}

