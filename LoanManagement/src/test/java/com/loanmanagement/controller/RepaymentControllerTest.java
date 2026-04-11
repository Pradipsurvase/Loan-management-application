package com.loanmanagement.controller;

import com.loanmanagement.service.RepaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepaymentController.class)
class RepaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepaymentService service;

    @Test
    void generate_success() throws Exception {
        mockMvc.perform(post("/api/repayment/generate/1"))
                .andExpect(status().isOk());
    }
    @Test
    void generate_invalidLoanId() throws Exception {
        mockMvc.perform(post("/api/repayment/generate/0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getSchedule_success() throws Exception {
        mockMvc.perform(get("/api/repayment/1"))
                .andExpect(status().isOk());
    }
    @Test
    void getSchedule_invalidLoanId() throws Exception {
        mockMvc.perform(get("/api/repayment/0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void pay_success() throws Exception {
        mockMvc.perform(post("/api/repayment/pay")
                        .param("loanId", "1")
                        .param("month", "1")
                        .param("amount", "1000"))
                .andExpect(status().isOk());
    }
    @Test
    void pay_invalidAmount() throws Exception {
        mockMvc.perform(post("/api/repayment/pay")
                        .param("loanId", "1")
                        .param("month", "1")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest());
    }
}