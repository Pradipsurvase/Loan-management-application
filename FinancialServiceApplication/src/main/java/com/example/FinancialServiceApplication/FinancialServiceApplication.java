package com.example.FinancialServiceApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FinancialServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialServiceApplication.class, args);
	}

}
