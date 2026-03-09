package com.example.educationloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EducationloanApplication {
    public static void main(String[] args) {
        SpringApplication.run(EducationloanApplication.class, args);
    }
}