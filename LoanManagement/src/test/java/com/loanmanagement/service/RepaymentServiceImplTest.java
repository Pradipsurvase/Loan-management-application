package com.loanmanagement.service;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.*;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.RepaymentRepository;
import com.loanmanagement.strategy.RepaymentStrategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepaymentServiceImplTest {

    @InjectMocks
    private RepaymentServiceImpl service;

    @Mock private RepaymentRepository repo;
    @Mock private LoanClient loanClient;
    @Mock private OverdraftService overdraftService;
    @Mock private RepaymentStrategy holidayStrategy;
    @Mock private RepaymentStrategy partPaymentStrategy;

    @Test
    void generateSchedule_success() {
        when(repo.existsByLoanId(1L)).thenReturn(false);

        LoanDto loan = mockLoan();
        when(loanClient.getLoan(1L)).thenReturn(loan);

        service.generateSchedule(1L);

        verify(repo, atLeastOnce()).save(any());
    }

    @Test
    void generateSchedule_alreadyExists() {
        when(repo.existsByLoanId(1L)).thenReturn(true);

        assertThrows(InvalidAmountException.class,
                () -> service.generateSchedule(1L));
    }

    // ================== PAY EMI ==================

    @Test
    void payEmi_success() {
        RepaymentSchedule r = mockSchedule();

        when(repo.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.of(r));

        service.payEmi(1L, 1, BigDecimal.valueOf(1000));

        verify(repo).save(any());
    }

    @Test
    void payEmi_notFound() {
        when(repo.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.payEmi(1L, 1, BigDecimal.valueOf(1000)));
    }

    @Test
    void payEmi_invalidAmount() {
        RepaymentSchedule r = mockSchedule();
        r.setEmi(BigDecimal.valueOf(1000));

        when(repo.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.of(r));

        assertThrows(InvalidAmountException.class,
                () -> service.payEmi(1L, 1, BigDecimal.valueOf(500)));
    }

    @Test
    void markHoliday_success() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setLoanId(1L);
        r.setMonth(1);
        r.setPaid(false);
        r.setHoliday(false);
        r.setEmi(BigDecimal.valueOf(1000));
        r.setBalance(BigDecimal.valueOf(10000));

        List<RepaymentSchedule> list = List.of(r);

        when(repo.findByLoanIdOrderByMonthAsc(1L)).thenReturn(list);

        doNothing().when(holidayStrategy)
                .apply(anyList(), eq(1L), eq(1), any());

        service.markHoliday(1L, 1);

        verify(holidayStrategy, times(1))
                .apply(anyList(), eq(1L), eq(1), any());
    }

    @Test
    void partPayment_success() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setLoanId(1L);
        r.setMonth(1);
        r.setPaid(false);
        r.setHoliday(false);
        r.setEmi(BigDecimal.valueOf(1000));
        r.setBalance(BigDecimal.valueOf(10000));

        List<RepaymentSchedule> list = List.of(r);

        when(repo.findByLoanIdOrderByMonthAsc(1L)).thenReturn(list);

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setLoanId(1L);
        dto.setMonth(1);
        dto.setAmount(BigDecimal.valueOf(2000));

        doNothing().when(partPaymentStrategy)
                .apply(anyList(), eq(1L), eq(1), any());

        service.partPayment(dto);

        verify(partPaymentStrategy, times(1))
                .apply(anyList(), eq(1L), eq(1), any());
    }
    @Test
    void getSchedule_success() {
        when(repo.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(List.of(mockSchedule()));

        assertNotNull(service.getSchedule(1L));
    }

    @Test
    void getSchedule_notFound() {
        when(repo.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getSchedule(1L));
    }
    private LoanDto mockLoan() {
        LoanDto loan = new LoanDto();
        loan.setPrincipal(BigDecimal.valueOf(10000));
        loan.setInterestRate(BigDecimal.valueOf(10));
        loan.setTenure(12);
        loan.setMoratorium(0);
        loan.setStartDate(LocalDate.now());
        return loan;
    }

    private RepaymentSchedule mockSchedule() {
        RepaymentSchedule r = new RepaymentSchedule();
        r.setLoanId(1L);
        r.setMonth(1);
        r.setEmi(BigDecimal.valueOf(1000));
        r.setBalance(BigDecimal.valueOf(10000));
        r.setPaid(false);
        r.setHoliday(false);
        return r;
    }
}