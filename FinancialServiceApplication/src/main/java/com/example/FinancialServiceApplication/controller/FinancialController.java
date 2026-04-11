package com.example.FinancialServiceApplication.controller;
import com.example.FinancialServiceApplication.dto.*;
import com.example.FinancialServiceApplication.service.FinancialService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/financial")
public class FinancialController {
    private final FinancialService service;
    public FinancialController(FinancialService service) {
        this.service = service;
    }
    @PostMapping("/schemes")
    public SchemeOptionsResponse getSchemes(@Valid @RequestBody SchemeRequest request) {
        return service.getSchemeOptions(request);
    }
    @PostMapping("/process")
    public FinancialResponse process(@Valid @RequestBody ProcessRequest request) {
        return service.process(request);
    }
}