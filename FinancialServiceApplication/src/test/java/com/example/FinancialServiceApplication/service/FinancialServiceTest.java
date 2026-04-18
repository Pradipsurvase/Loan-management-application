package com.example.FinancialServiceApplication.service;

import com.example.FinancialServiceApplication.client.LoanClient;
import com.example.FinancialServiceApplication.client.UserClient;
import com.example.FinancialServiceApplication.dto.ProcessRequest;
import com.example.FinancialServiceApplication.dto.FinancialResponse;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.exception.InvalidSchemeSelectionException;
import com.example.FinancialServiceApplication.exception.LoanNotFoundException;
import com.example.FinancialServiceApplication.exception.UserNotFoundException;
import com.example.FinancialServiceApplication.service.charges.*;
import com.example.FinancialServiceApplication.service.scheme.*;
import com.example.FinancialServiceApplication.validation.FinancialValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FinancialServiceTest {

    @InjectMocks
    private FinancialService financialService;

    @Mock
    private SchemeService schemeService;

    @Mock
    private ChargesService chargesService;

    @Mock
    private LoanClient loanClient;

    @Mock
    private UserClient userClient;

    @Mock
    private FinancialValidator validator;

    @Test
    void testProcess_success() {
        ProcessRequest request = new ProcessRequest();
        request.setLoanId(1L);
        request.setBankName("HDFC");
        request.setSelectedScheme("CSIS");

        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .amount(100000)
                .interestRate(10)
                .userId(10L)
                .build();

        UserDetails user = new UserDetails(10L, 300000, "GENERAL", "MH");

        SchemeResult schemeResult =
                new SchemeResult("CSIS", 20000, 80000, 100000);

        ChargeResult chargeResult =
                new ChargeResult(5000, Map.of());

        when(loanClient.getLoan(1L)).thenReturn(loan);
        when(userClient.getUser(10L)).thenReturn(user);
        when(schemeService.applySelectedScheme(any(), any(), any()))
                .thenReturn(schemeResult);
        when(chargesService.applyCharges(any(), any()))
                .thenReturn(chargeResult);

        FinancialResponse response = financialService.process(request);

        assertEquals(100000, response.getOriginalLoan());
        assertEquals(80000, response.getUpdatedLoan());
        assertEquals(20000, response.getSubsidy());
        assertEquals(5000, response.getCharges());
        assertEquals(85000, response.getFinalPayable());
    }


    @Test
    void testProcess_invalidScheme() {
        ProcessRequest request = new ProcessRequest();
        request.setLoanId(1L);
        request.setBankName("HDFC");
        request.setSelectedScheme("INVALID");

        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .amount(100000)
                .interestRate(10)
                .userId(10L)
                .build();

        UserDetails user = new UserDetails(10L, 300000, "GENERAL", "MH");

        when(loanClient.getLoan(1L)).thenReturn(loan);
        when(userClient.getUser(10L)).thenReturn(user);

        when(schemeService.applySelectedScheme(any(), any(), any()))
                .thenThrow(new InvalidSchemeSelectionException("INVALID"));

        assertThrows(InvalidSchemeSelectionException.class,
                () -> financialService.process(request));
    }


    @Test
    void testProcess_loanNotFound() {
        ProcessRequest request = new ProcessRequest();
        request.setLoanId(1L);

        when(loanClient.getLoan(1L)).thenReturn(null);

        assertThrows(LoanNotFoundException.class,
                () -> financialService.process(request));
    }


    @Test
    void testProcess_userNotFound() {
        ProcessRequest request = new ProcessRequest();
        request.setLoanId(1L);

        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .userId(10L)
                .build();

        when(loanClient.getLoan(1L)).thenReturn(loan);
        when(userClient.getUser(10L)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> financialService.process(request));
    }


    @Test
    void testProcess_validationFails() {
        ProcessRequest request = new ProcessRequest();
        request.setLoanId(1L);

        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .userId(10L)
                .build();

        UserDetails user = new UserDetails(10L, 300000, "GENERAL", "MH");

        when(loanClient.getLoan(1L)).thenReturn(loan);
        when(userClient.getUser(10L)).thenReturn(user);

        doThrow(new RuntimeException("Invalid loan"))
                .when(validator).validate(any());

        assertThrows(RuntimeException.class,
                () -> financialService.process(request));
    }


    @Test
    void testProcess_zeroCharges() {
        ProcessRequest request = new ProcessRequest();
        request.setLoanId(1L);
        request.setBankName("HDFC");
        request.setSelectedScheme("CSIS");

        LoanDetails loan = LoanDetails.builder()
                .loanId(1L)
                .amount(100000)
                .userId(10L)
                .build();

        UserDetails user = new UserDetails(10L, 300000, "GENERAL", "MH");

        SchemeResult schemeResult =
                new SchemeResult("CSIS", 20000, 80000, 100000);

        ChargeResult chargeResult =
                new ChargeResult(0, Map.of());

        when(loanClient.getLoan(1L)).thenReturn(loan);
        when(userClient.getUser(10L)).thenReturn(user);
        when(schemeService.applySelectedScheme(any(), any(), any()))
                .thenReturn(schemeResult);
        when(chargesService.applyCharges(any(), any()))
                .thenReturn(chargeResult);

        FinancialResponse response = financialService.process(request);

        assertEquals(0, response.getCharges());
    }
}