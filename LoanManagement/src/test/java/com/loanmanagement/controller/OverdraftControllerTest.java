package com.loanmanagement.controller;

import com.loanmanagement.dto.OverdraftResponseDto;
import com.loanmanagement.service.OverdraftService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OverdraftController.class)
class OverdraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OverdraftService service;
    @Test
    void getOD_success() throws Exception {

        OverdraftResponseDto dto = new OverdraftResponseDto();
        dto.setLoanId(1L);

        when(service.getOD(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/overdraft/1"))
                .andExpect(status().isOk());
    }
    @Test
    void getOD_invalidLoanId() throws Exception {

        mockMvc.perform(get("/api/overdraft/0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void withdraw_success() throws Exception {

        mockMvc.perform(post("/api/overdraft/withdraw")
                        .param("loanId", "1")
                        .param("amount", "1000"))
                .andExpect(status().isOk());
    }
    @Test
    void withdraw_invalidAmount() throws Exception {

        mockMvc.perform(post("/api/overdraft/withdraw")
                        .param("loanId", "1")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void repay_success() throws Exception {

        mockMvc.perform(post("/api/overdraft/repay")
                        .param("loanId", "1")
                        .param("amount", "1000"))
                .andExpect(status().isOk());
    }
    @Test
    void repay_invalidAmount() throws Exception {

        mockMvc.perform(post("/api/overdraft/repay")
                        .param("loanId", "1")
                        .param("amount", "-10"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void interest_success() throws Exception {

        when(service.calculateInterest(1L))
                .thenReturn(BigDecimal.valueOf(100));

        mockMvc.perform(get("/api/overdraft/interest/1"))
                .andExpect(status().isOk());
    }
}