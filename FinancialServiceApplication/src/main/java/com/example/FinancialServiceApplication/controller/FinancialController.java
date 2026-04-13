package com.example.FinancialServiceApplication.controller;

import com.example.FinancialServiceApplication.dto.*;
import com.example.FinancialServiceApplication.service.FinancialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/financial")
public class FinancialController {

    private final FinancialService service;
    public FinancialController(FinancialService service) {
        this.service = service;
    }

    @PostMapping("/schemes")
    public ResponseEntity<SchemeOptionsResponse> getSchemes(
            @Valid @RequestBody SchemeRequest request) {
        SchemeOptionsResponse response = service.getSchemeOptions(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/process")
    public ResponseEntity<FinancialResponse> process(
            @Valid @RequestBody ProcessRequest request) {
        FinancialResponse response = service.process(request);
        return ResponseEntity.ok(response);
    }
}