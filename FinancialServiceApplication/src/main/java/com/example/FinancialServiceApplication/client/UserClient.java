package com.example.FinancialServiceApplication.client;
import com.example.FinancialServiceApplication.entity.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {
    @GetMapping("/user/{id}")
    UserDetails getUser(@PathVariable("id") Long id);
}