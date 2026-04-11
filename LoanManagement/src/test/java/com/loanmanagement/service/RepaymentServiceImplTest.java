package com.loanmanagement.service;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.dto.PaymentRequestDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.RepaymentRepository;
import com.loanmanagement.strategy.RepaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RepaymentServiceImplTest {

    @Mock private RepaymentRepository repository;
    @Mock private OverdraftService overdraftService;
    @Mock private LoanClient loanClient;
    @Mock private RepaymentStrategy holidayStrategy;
    @Mock private RepaymentStrategy partPaymentStrategy;

    @InjectMocks
    private RepaymentServiceImpl service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "prepaymentPercent", BigDecimal.valueOf(2));
    }

    private LoanDto loan() {
        LoanDto l = new LoanDto();
        l.setLoanId(1L);
        l.setPrincipal(BigDecimal.valueOf(100000));
        l.setInterestRate(BigDecimal.valueOf(10));
        l.setTenure(12);
        l.setMoratorium(2);
        l.setStartDate(LocalDate.now().minusMonths(12));
        l.setLockInMonths(6);
        return l;
    }
    @Test
    void generateSchedule_success() {
        when(repository.existsByLoanId(1L)).thenReturn(false);
        when(loanClient.getLoan(1L)).thenReturn(loan());

        service.generateSchedule(1L);

        verify(repository, atLeastOnce()).save(any());
        verify(overdraftService).createOD(eq(1L), any());
    }

    @Test
    void generateSchedule_exists() {
        when(repository.existsByLoanId(1L)).thenReturn(true);

        assertThrows(InvalidAmountException.class,
                () -> service.generateSchedule(1L));
    }
    @Test
    void getEmi_success() {
        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);

        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.of(r));

        assertNotNull(service.getEmi(1L, 1));
    }
    @Test
    void getEmi_invalidMonth() {
        assertThrows(InvalidAmountException.class,
                () -> service.getEmi(1L, 0));
    }

    @Test
    void getEmi_notFound() {
        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getEmi(1L, 1));
    }
    @Test
    void markHoliday_success() {
        List<RepaymentSchedule> list = List.of(new RepaymentSchedule());

        when(repository.findByLoanIdOrderByMonthAsc(1L)).thenReturn(list);

        service.markHoliday(1L, 1);

        verify(holidayStrategy).apply(any(), eq(1L), eq(1), any());
        verify(repository).saveAll(list);
    }
    @Test
    void partPayment_success() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setLoanId(1L);
        dto.setAmount(BigDecimal.valueOf(6000));
        dto.setMonth(1);

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(List.of(new RepaymentSchedule()));

        service.partPayment(dto);

        verify(partPaymentStrategy).apply(any(), eq(1L), eq(1), any());
    }
}