package com.loanmanagement.service;

import com.loanmanagement.entity.Overdraft;
import com.loanmanagement.enums.Status;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.repository.OverdraftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OverdraftServiceImplTest {

    @Mock
    private OverdraftRepository repo;

    @InjectMocks
    private OverdraftServiceImpl service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "interestRate", 12.0);
        ReflectionTestUtils.setField(service, "limitPercent", 20.0);
        ReflectionTestUtils.setField(service, "tenureMonths", 12);
    }

    private Overdraft getOD() {
        Overdraft od = new Overdraft();
        od.setLoanId(1L);
        od.setUsedAmount(BigDecimal.valueOf(2000));
        od.setLimitAmount(BigDecimal.valueOf(10000));
        od.setInterestRate(12.0);
        od.setStatus(Status.ACTIVE);
        od.setEndDate(LocalDate.now().plusMonths(1));
        return od;
    }
    @Test
    void withdraw_success() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(getOD()));

        service.withdraw(1L, BigDecimal.valueOf(1000));

        verify(repo).save(any());
    }
    @Test
    void withdraw_limitExceeded() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(getOD()));

        assertThrows(InvalidAmountException.class,
                () -> service.withdraw(1L, BigDecimal.valueOf(9000)));
    }
    @Test
    void withdraw_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> service.withdraw(1L, BigDecimal.ZERO));
    }
    @Test
    void repay_success() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(getOD()));

        service.repay(1L, BigDecimal.valueOf(1000));

        verify(repo).save(any());
    }
    @Test
    void repay_exceedsUsed() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(getOD()));

        assertThrows(InvalidAmountException.class,
                () -> service.repay(1L, BigDecimal.valueOf(5000)));
    }
}