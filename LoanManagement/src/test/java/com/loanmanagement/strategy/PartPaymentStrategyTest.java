package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartPaymentStrategyTest {

    @Mock
    private LoanClient loanClient;

    @InjectMocks
    private PartPaymentStrategy strategy;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(strategy, "minPartPayment", BigDecimal.valueOf(5000));
    }

    private List<RepaymentSchedule> getList() {
        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);
        r.setBalance(BigDecimal.valueOf(10000));
        r.setPaid(false);
        return List.of(r);
    }
    @Test
    void partPayment_success() {
        LoanDto loan = new LoanDto();
        loan.setInterestRate(BigDecimal.valueOf(10));

        when(loanClient.getLoan(1L)).thenReturn(loan);

        List<RepaymentSchedule> list = getList();
        strategy.apply(list, 1L, 1, BigDecimal.valueOf(6000));
        assertTrue(list.get(0).getBalance().compareTo(BigDecimal.ZERO) >= 0);
    }
    @Test
    void partPayment_lessAmount() {
        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(getList(), 1L, 1, BigDecimal.valueOf(1000)));
    }
    @Test
    void partPayment_exceedsBalance() {
        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(getList(), 1L, 1, BigDecimal.valueOf(20000)));
    }
}