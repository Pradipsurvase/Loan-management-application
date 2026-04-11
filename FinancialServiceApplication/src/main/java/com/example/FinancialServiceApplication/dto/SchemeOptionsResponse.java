package com.example.FinancialServiceApplication.dto;

import com.example.FinancialServiceApplication.service.scheme.SchemeResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SchemeOptionsResponse {

    private List<SchemeResult> eligibleSchemes;
    private String recommendedScheme;
}