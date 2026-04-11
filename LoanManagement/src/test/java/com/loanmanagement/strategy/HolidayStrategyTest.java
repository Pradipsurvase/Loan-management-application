package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayStrategyTest {

    @Mock
    private LoanClient loanClient;

    @InjectMocks
    private HolidayStrategy strategy;

    @Test
    void holiday_success() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);
        r.setBalance(BigDecimal.valueOf(10000));
        r.setPaid(false);
        r.setHoliday(false);

        List<RepaymentSchedule> list = List.of(r);

        LoanDto loan = new LoanDto();
        loan.setInterestRate(BigDecimal.valueOf(10));

        when(loanClient.getLoan(1L)).thenReturn(loan);

        strategy.apply(list, 1L, 1, BigDecimal.ZERO);

        assertTrue(r.isHoliday());
        assertEquals(BigDecimal.ZERO, r.getEmi());
    }

    @Test
    void holiday_invalidMonth() {
        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(List.of(), 1L, 1, BigDecimal.ZERO));
    }
}