package com.loanmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoanManagementApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void mainMethodTest() {
		// This explicitly calls the main method to satisfy JaCoCo
		LoanManagementApplication.main(new String[] {});
	}

}
