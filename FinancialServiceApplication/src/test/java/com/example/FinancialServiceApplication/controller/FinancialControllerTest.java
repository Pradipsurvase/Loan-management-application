package com.example.FinancialServiceApplication.controller;

import com.example.FinancialServiceApplication.dto.FinancialResponse;
import com.example.FinancialServiceApplication.service.FinancialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialController.class)
class FinancialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinancialService financialService;

    @Test
    void testProcessAPI() throws Exception {

        String requestJson = """
            {
              "loanId": 1,
              "bankName": "HDFC",
              "selectedScheme": "CSIS"
            }
        """;

        FinancialResponse response = FinancialResponse.builder()
                .originalLoan(100000)
                .updatedLoan(80000)
                .subsidy(20000)
                .charges(5000)
                .finalPayable(85000)
                .build();

        when(financialService.process(any())).thenReturn(response);

        mockMvc.perform(post("/financial/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPayable").value(85000));
    }
}