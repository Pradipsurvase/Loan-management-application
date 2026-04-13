package com.loanmanagement.mapper;

import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanStatus;
import com.loanmanagement.enums.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationMapperTest {

    private LoanApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LoanApplicationMapper();
    }

    @Test
    @DisplayName("toResponse - Should map all fields correctly (Positive)")
    void toResponse_Positive() {
        LocalDateTime now = LocalDateTime.now();
        LoanApplication loan = LoanApplication.builder()
                .loanId("L100")
                .applicationNumber("APP-2026-XYZ")
                .status(LoanStatus.SUBMITTED)
                .applicantName("Reshma")
                .gender(Gender.FEMALE)
                .bankId("B1")
                .bankName("National Bank")
                .bankInterestRateId("R1")
                .loanType(LoanType.DOMESTIC)
                .interestType(InterestType.FIXED)
                .baseInterestRate(new BigDecimal("8.50"))
                .appliedInterestRate(new BigDecimal("9.00"))
                .loanAmount(new BigDecimal("15000.00"))
                .courseFee(new BigDecimal("10000.00"))
                .livingExpenses(new BigDecimal("2000.00"))
                .travelExpenses(new BigDecimal("1000.00"))
                .bookEquipmentCost(new BigDecimal("500.00"))
                .insuranceCoverage(new BigDecimal("1000.00"))
                .laptopAllowance(new BigDecimal("500.00"))
                .studyPeriodMonths(12)
                .gracePeriodMonths(6)
                .moratoriumPeriod(18)
                .moratoriumInterest(new BigDecimal("1200.00"))
                .principalAfterMoratorium(new BigDecimal("16200.00"))
                .totalRepayableAmount(new BigDecimal("18000.00"))
                .subsidized(true)
                .subsidyScheme("Vidya Lakshmi")
                .createdAt(now)
                .updatedAt(now)
                .build();

        LoanApplicationResponseDTO response = mapper.toResponse(loan);

        assertNotNull(response);
        assertEquals("L100", response.getLoanId());
        assertEquals("APP-2026-XYZ", response.getApplicationNumber());
        assertEquals("SUBMITTED", response.getStatus());

        assertEquals("Reshma", response.getApplicant().getName());
        assertEquals("FEMALE", response.getApplicant().getGender());

        assertEquals("B1", response.getBank().getId());
        assertEquals("National Bank", response.getBank().getName());

        assertEquals("DOMESTIC", response.getLoanDetails().getLoanType());
        assertEquals(new BigDecimal("15000.00"), response.getLoanDetails().getLoanAmount());

        assertEquals(new BigDecimal("10000.00"), response.getExpenses().getCourseFee());
        assertEquals(new BigDecimal("500.00"), response.getExpenses().getLaptopAllowance());

        assertEquals(18, response.getRepayment().getMoratoriumPeriodMonths());
        assertEquals(new BigDecimal("18000.00"), response.getRepayment().getTotalRepayableAmount());

        assertNotNull(response.getSubsidy());
        assertTrue(response.getSubsidy().getIsSubsidized());
        assertEquals("Vidya Lakshmi", response.getSubsidy().getScheme());

        assertEquals(now, response.getTimestamps().getCreatedAt());
    }

    @Test
    @DisplayName("toResponse - Should handle null status (Negative/Edge Case)")
    void toResponse_NullStatus() {
        LoanApplication loan = new LoanApplication();
        loan.setStatus(null); // Explicitly null for coverage
        loan.setGender(Gender.MALE); // Set required Enums to avoid NPE in mapper
        loan.setLoanType(LoanType.INTERNATIONAL);
        loan.setInterestType(InterestType.FLOATING);

        LoanApplicationResponseDTO response = mapper.toResponse(loan);

        assertNull(response.getStatus(), "Status should be null when entity status is null");
    }

    @Test
    @DisplayName("toResponse - Should throw exception if required Enums are missing")
    void toResponse_NPE_OnRequiredFields() {
        LoanApplication loan = new LoanApplication();

        assertThrows(NullPointerException.class, () -> mapper.toResponse(loan));
    }
}