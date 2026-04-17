package com.loanmanagement.dto;

import com.loanmanagement.entity.Bank;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.MarketRate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MockDataWrapper {
    private List<Bank> banks;
    private List<BankInterestRate> rates;
    private List<MarketRate> marketRates;
}