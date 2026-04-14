package com.example.FinancialServiceApplication.client;

import com.example.FinancialServiceApplication.dto.LoanSchemeUpdateRequest;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "loan-service")
public interface LoanClient {

    @GetMapping("/loan/{id}")
    LoanDetails getLoan(@PathVariable("id") Long id);

    @PostMapping("/loan/update-scheme")
    void sendSchemeData(@RequestBody LoanSchemeUpdateRequest request);
}