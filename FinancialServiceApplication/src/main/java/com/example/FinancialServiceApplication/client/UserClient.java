package com.example.FinancialServiceApplication.client;

import com.example.FinancialServiceApplication.entity.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {

    @GetMapping("/user/{id}")
    UserDetails getUser(@PathVariable("id") Long id);
}
