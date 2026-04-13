package com.loanmanagement.service;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.dto.LoanCalculationResponseDTO;
import com.loanmanagement.entity.Bank;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.InterestCalculationResult;
import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.enums.Gender;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import com.loanmanagement.exception.LoanBusinessException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.mapper.LoanApplicationMapper;
import com.loanmanagement.repository.LoanRepository;
import com.loanmanagement.service.repayment.InterestCalculationService;
import com.loanmanagement.service.repayment.LoanServiceImpl;
import com.loanmanagement.service.repayment.MockDataStore;
import com.loanmanagement.util.LoanValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private MockDataStore mockDataStore;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private InterestCalculationService interestCalculationService;
    @Mock
    private LoanApplicationMapper mapper;
    @Mock
    private LoanValidationUtils loanValidationUtils;

    @InjectMocks
    private LoanServiceImpl loanService;

    private LoanApplicationRequestDTO requestDTO;
    private Bank mockBank;
    private BankInterestRate mockRate;
    private InterestCalculationResult mockCalc;

    @BeforeEach
    void setUp() {
        requestDTO = new LoanApplicationRequestDTO();
        requestDTO.setBankId("B1");
        requestDTO.setBankInterestRateId("R1");
        requestDTO.setApplicantName("Reshma");
        requestDTO.setGender(Gender.FEMALE);
        requestDTO.setLoanType(LoanType.DOMESTIC);
        requestDTO.setInterestType(InterestType.FIXED);
        requestDTO.setCourseFee(new BigDecimal("5000"));
        requestDTO.setStudyPeriodMonths(12);
        requestDTO.setGracePeriodMonths(6);
        requestDTO.setRequestedTenureMonths(60);

        mockBank = new Bank();
        mockBank.setId("B1");
        mockBank.setBankCode("SBI");
        mockBank.setBankName("State Bank");
        mockBank.setIsActive(true);

        mockRate = new BankInterestRate();
        mockRate.setId("R1");
        mockRate.setBaseRate(new BigDecimal("9.0"));
        mockRate.setIsActive(true);

        mockCalc = new InterestCalculationResult(
                new BigDecimal("9.5"),
                new BigDecimal("400"),
                new BigDecimal("5400"),
                new BigDecimal("6000"),
                18
        );
    }


    @Test
    @DisplayName("Should successfully submit loan application")
    void submitApplication_Success() {
        // Arrange
        when(mockDataStore.findBankById(anyString())).thenReturn(Optional.of(mockBank));
        when(mockDataStore.findRateById(anyString())).thenReturn(Optional.of(mockRate));
        when(loanValidationUtils.validateAndCalculateLoanAmount(any(), any())).thenReturn(new BigDecimal("5000"));
        when(interestCalculationService.calculate(any(), any(), any(), anyInt(), anyInt(), anyInt())).thenReturn(mockCalc);
        when(mapper.toResponse(any(LoanApplication.class))).thenReturn(new LoanApplicationResponseDTO());

        LoanApplicationResponseDTO result = loanService.submitApplication(requestDTO);

        assertNotNull(result);
        verify(loanRepository, times(1)).save(any(LoanApplication.class));
        verify(mockDataStore).findBankById("B1");
    }

    @Test
    @DisplayName("Should successfully calculate loan without saving")
    void calculateLoan_Success() {
        // 1. Mock the Bank (This was missing!)
        when(mockDataStore.findBankById("B1")).thenReturn(Optional.of(mockBank));

        // 2. Mock the Rate
        when(mockDataStore.findRateById("R1")).thenReturn(Optional.of(mockRate));

        // 3. Mock the Utils and Service
        when(loanValidationUtils.validateAndCalculateLoanAmount(any(), any())).thenReturn(new BigDecimal("5000"));
        when(interestCalculationService.calculate(any(), any(), any(), anyInt(), anyInt(), anyInt())).thenReturn(mockCalc);

        // Act
        LoanCalculationResponseDTO result = loanService.calculateLoan(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("9.5"), result.getAppliedInterestRate());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully retrieve loan by application number")
    void getLoanByAppNumber_Success() {
        LoanApplication app = new LoanApplication();
        when(loanRepository.findByApplicationNumber("APP123")).thenReturn(Optional.of(app));
        when(mapper.toResponse(app)).thenReturn(new LoanApplicationResponseDTO());

        LoanApplicationResponseDTO result = loanService.getLoanByApplicationNumber("APP123");


        assertNotNull(result);
        verify(loanRepository).findByApplicationNumber("APP123");
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException when Bank ID is invalid")
    void fetchBank_NotFound() {
        // Arrange: Ensure the data store returns empty for this specific ID
        String invalidBankId = "INVALID_BANK_ID";
        when(mockDataStore.findBankById(invalidBankId)).thenReturn(Optional.empty());
        requestDTO.setBankId(invalidBankId);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> loanService.submitApplication(requestDTO));

        assertTrue(exception.getMessage().contains("Bank not found"));
        verify(mockDataStore).findBankById(invalidBankId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when Rate ID is invalid")
    void fetchRate_NotFound() {
        // Arrange:
        // 1. Bank must be found AND must be active to pass the first check
        mockBank.setIsActive(true);
        when(mockDataStore.findBankById(anyString())).thenReturn(Optional.of(mockBank));

        // 2. Rate is the one we want to fail
        String invalidRateId = "INVALID_RATE_ID";
        when(mockDataStore.findRateById(invalidRateId)).thenReturn(Optional.empty());
        requestDTO.setBankInterestRateId(invalidRateId);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> loanService.submitApplication(requestDTO));

        assertTrue(exception.getMessage().contains("Rate not found"));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when Application Number does not exist")
    void getLoan_NotFound() {
        when(loanRepository.findByApplicationNumber("NON-EXISTENT")).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> loanService.getLoanByApplicationNumber("NON-EXISTENT"));

        assertTrue(ex.getMessage().contains("Loan not found"));
    }
    @Test
    @DisplayName("Should throw LoanBusinessException if application already exists")
    void submitApplication_AlreadyExists() {
        when(loanRepository.existsByApplicantNameAndBankId(anyString(), anyString())).thenReturn(true);

        assertThrows(LoanBusinessException.class, () -> loanService.submitApplication(requestDTO));
        verify(loanRepository, never()).save(any());
    }
}