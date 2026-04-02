package com.loanmanagement.service.repayment;

import com.loanmanagement.dto.MockDataWrapper;
import com.loanmanagement.dto.RateJsonDTO;
import com.loanmanagement.entity.Bank;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.MarketRate;
import com.loanmanagement.enums.InterestType;
import com.loanmanagement.enums.LoanType;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class MockDataStore {

    private final Map<String, Bank> banks = new LinkedHashMap<>();
    private final Map<String, BankInterestRate> rates = new LinkedHashMap<>();
    private final Map<String, MarketRate> marketRates = new LinkedHashMap<>();

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = new ClassPathResource("mock-data.json").getInputStream();

            MockDataWrapper data = mapper.readValue(input, MockDataWrapper.class);


                for (Bank bank : data.getBanks()) {
                    bank.setInterestRates(new ArrayList<>()); // mock reverse mapping
                    banks.put(bank.getId(), bank);
                }


            for (BankInterestRate dto : data.getRates()) {
                Bank bank = banks.get(dto.getBankId());
                if (bank == null) {
                    throw new RuntimeException("Invalid bankId in JSON: " + dto.getBankId());
                }

                for (MarketRate market : data.getMarketRates()) {
                    marketRates.put(market.getBenchmarkName(), market);
                }

                BankInterestRate rate = BankInterestRate.builder()
                        .id(dto.getId())
                        .bankId(dto.getBankId())   // Only ID, DB save not needed
                        .loanType(dto.getLoanType())
                        .interestType(dto.getInterestType())
                        .baseRate(dto.getBaseRate())
                        .floatingSpread(dto.getFloatingSpread())
                        .benchmarkName(dto.getBenchmarkName())
                        .domesticDiscount(dto.getDomesticDiscount())
                        .femaleDiscount(dto.getFemaleDiscount())
                        .subsidyRate(dto.getSubsidyRate())
                        .minLoanAmount(dto.getMinLoanAmount())
                        .maxLoanAmount(dto.getMaxLoanAmount())
                        .minTenureMonths(dto.getMinTenureMonths())
                        .maxTenureMonths(dto.getMaxTenureMonths())
                        .maxMoratoriumMonths(dto.getMaxMoratoriumMonths())
                        .minGracePeriodMonths(dto.getMinGracePeriodMonths())
                        .maxGracePeriodMonths(dto.getMaxGracePeriodMonths())
                        .effectiveFrom(LocalDate.now())
                        .effectiveTo(null)
                        .isActive(dto.getIsActive())
                        .bank(bank) // @Transient only for filtering
                        .build();

                rates.put(rate.getId(), rate);

                // Reverse mapping for mock filtering
                bank.getInterestRates().add(rate);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON mock data", e);
        }
    }

    // Fetch all active banks
    public List<Bank> getAllActiveBanks() {
        return banks.values().stream()
                .filter(Bank::getIsActive)
                .toList();
    }

    // Find bank by ID
    public Optional<Bank> findBankById(String id) {
        return Optional.ofNullable(banks.get(id));
    }

    // Get active rates for a bank (filter by loan & interest type)
    public List<BankInterestRate> getActiveRatesForBank(String bankId,
                                                        LoanType loanType,
                                                        InterestType interestType) {
        return rates.values().stream()
                .filter(r -> r.getBankId().equals(bankId)) // Use bankId
                .filter(BankInterestRate::getIsActive)
                .filter(r -> r.getEffectiveTo() == null)
                .filter(r -> loanType == null || r.getLoanType() == loanType)
                .filter(r -> interestType == null || r.getInterestType() == interestType)
                .toList();
    }

    // Find rate by ID
    public Optional<BankInterestRate> findRateById(String rateId) {
        return Optional.ofNullable(rates.get(rateId));
    }


    public MarketRate getActiveMarketRate(String benchmarkName) {
        MarketRate rate = marketRates.get(benchmarkName);

        if (rate == null || !rate.getIsActive()) {
            throw new RuntimeException("Market rate not found for: " + benchmarkName);
        }

        return rate;
    }
}





