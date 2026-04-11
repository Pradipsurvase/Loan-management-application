package com.example.FinancialServiceApplication.service.scheme;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemeResult {
    private String schemeName;
    private double benefit;
    private double updatedLoanAmount;
    private double originalLoan;
    public String getSchemeName() {
        return schemeName;
    }
    public double getBenefit() {
        return benefit;
    }
    public double getUpdatedLoanAmount() {
        return updatedLoanAmount;
    }
}