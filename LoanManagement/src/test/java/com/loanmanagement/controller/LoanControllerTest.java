package com.loanmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.dto.LoanCalculationResponseDTO;
import com.loanmanagement.service.repayment.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanApplicationRequestDTO validRequest() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO();

        dto.setBankId("B1");
        dto.setBankInterestRateId("R1");
        dto.setApplicantName("Reshma");
        dto.setGender(com.loanmanagement.enums.Gender.FEMALE);
        dto.setLoanType(com.loanmanagement.enums.LoanType.DOMESTIC);
        dto.setInterestType(com.loanmanagement.enums.InterestType.FIXED);
        dto.setStudyPeriodMonths(12);
        dto.setGracePeriodMonths(6);
        dto.setRequestedTenureMonths(60);
        dto.setCourseFee(new BigDecimal("50000"));
        return dto;
    }

    private LoanApplicationResponseDTO buildResponse() {
        LoanApplicationResponseDTO dto = new LoanApplicationResponseDTO();
        dto.setApplicationNumber("APP123");
        return dto;
    }


    @Test
    void submitApplication_success() throws Exception {

        when(loanService.submitApplication(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/api/loan/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationNumber").value("APP123"));
    }

    @Test
    @DisplayName("Should return accurate interest calculations based on request")
    void calculateLoan_Success() throws Exception {
        // Arrange: Create a response matching your specific DTO
        LoanCalculationResponseDTO calcResponse = new LoanCalculationResponseDTO();
        calcResponse.setLoanAmount(new BigDecimal("50000"));
        calcResponse.setAppliedInterestRate(new BigDecimal("9.5"));
        calcResponse.setMoratoriumPeriod(18);
        calcResponse.setMoratoriumInterest(new BigDecimal("1200.50"));
        calcResponse.setPrincipalAfterMoratorium(new BigDecimal("51200.50"));
        calcResponse.setTotalRepayableAmount(new BigDecimal("65000.00"));

        when(loanService.calculateLoan(any(LoanApplicationRequestDTO.class)))
                .thenReturn(calcResponse);

        mockMvc.perform(post("/api/loan/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                // Assert all fields from your LoanCalculationResponseDTO
                .andExpect(jsonPath("$.loanAmount").value(50000))
                .andExpect(jsonPath("$.appliedInterestRate").value(9.5))
                .andExpect(jsonPath("$.moratoriumPeriod").value(18))
                .andExpect(jsonPath("$.moratoriumInterest").value(1200.50))
                .andExpect(jsonPath("$.principalAfterMoratorium").value(51200.50))
                .andExpect(jsonPath("$.totalRepayableAmount").value(65000.00));
    }

    @Test
    void getLoan_Success() throws Exception {
        when(loanService.getLoanByApplicationNumber("APP123")).thenReturn(buildResponse());

        mockMvc.perform(get("/api/loan/APP123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationNumber").value("APP123"));
    }

    @Test
    void submitApplication_validationFail() throws Exception {

        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO();

        mockMvc.perform(post("/api/loan/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLoan_NotFound() throws Exception {
        when(loanService.getLoanByApplicationNumber("INVALID")).thenReturn(null);

        mockMvc.perform(get("/api/loan/INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitApplication_WrongMediaType() throws Exception {
        mockMvc.perform(post("/api/loan/submit")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Plain Text Body"))
                .andExpect(status().isUnsupportedMediaType());
    }
}