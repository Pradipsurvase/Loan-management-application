package com.example.FinancialServiceApplication.client;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "loan-service", url = "http://localhost:8081")
public interface LoanClient {

    @GetMapping("/loan/{id}")
    LoanDetails getLoan(@PathVariable("id") Long id);
}

