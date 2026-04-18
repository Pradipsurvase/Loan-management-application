package com.example.FinancialServiceApplication;

import com.example.FinancialServiceApplication.client.LoanClient;
import com.example.FinancialServiceApplication.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class FinancialServiceApplicationTests {

	@MockBean
	private LoanClient loanClient;

	@MockBean
	private UserClient userClient;

	@Test
	void contextLoads() {
	}
}