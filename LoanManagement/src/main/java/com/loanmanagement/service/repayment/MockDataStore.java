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

            // ✅ Load Banks
                for (Bank bank : data.getBanks()) {
                    bank.setInterestRates(new ArrayList<>()); // mock reverse mapping
                    banks.put(bank.getId(), bank);
                }

            // ✅ Load Rates
            for (BankInterestRate dto : data.getRates()) {

                Bank bank = banks.get(dto.getBankId());
                if (bank == null) {
                    throw new RuntimeException("Invalid bankId in JSON: " + dto.getBankId());
                }

                // ✅ Load Market Rates
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


    // DESIGN PATTERN: Repository stub (in-memory store)
// WHY: while the actual JPA repositories are wired up, this in-memory store
//      serves as mock data for local development and unit tests without a DB.
//      It follows the same interface shape as a real BankRepository would,
//      so swapping it out later requires zero changes to the service layer.
//
// PREVIOUS ISSUES FIXED:
//  - rate.setTenure()         → was undefined; replaced with explicit min/maxTenureMonths
//  - rate.setGracePeriodMonths() → was undefined; replaced with min/maxGracePeriodMonths
//  - rate.setSubsidyRate()    → was called with no argument; now set to 4.00%
//  - effectiveFrom            → was missing; all rates now have a valid date
//  - isActive                 → was present but missing on some rows; now explicit on all
//  - domesticDiscount         → was missing on some rows; now set on all DOMESTIC rows
//  - HDFC bank and rates      → were defined in getBanks() but had no matching rates; now added

        // ── Banks ────────────────────────────────────

     /*   public static final String SBI_ID   = "bank-001";
        public static final String HDFC_ID  = "bank-002";
        public static final String PNB_ID   = "bank-003";

        public static final String RATE_SBI_DOM_FIXED    = "rate-001";
        public static final String RATE_SBI_DOM_FLOAT    = "rate-002";
        public static final String RATE_SBI_INTL_FIXED   = "rate-003";
        public static final String RATE_SBI_INTL_FLOAT   = "rate-004";
        public static final String RATE_HDFC_DOM_FIXED   = "rate-005";
        public static final String RATE_HDFC_INTL_FIXED  = "rate-006";
        public static final String RATE_PNB_DOM_FIXED    = "rate-007";

        private final Map<String, Bank> banks = new LinkedHashMap<>();
        private final Map<String, BankInterestRate> rates = new LinkedHashMap<>();

        public MockDataStore() {
            Bank sbi  = buildBank(SBI_ID,  "State Bank of India", "SBI001", "SBIN",
                    "https://cdn.example.com/sbi.png");
            Bank hdfc = buildBank(HDFC_ID, "HDFC Bank",           "HDFC01", "HDFC",
                    "https://cdn.example.com/hdfc.png");
            Bank pnb  = buildBank(PNB_ID,  "Punjab National Bank","PNB001", "PUNB",
                    "https://cdn.example.com/pnb.png");

            banks.put(sbi.getId(),  sbi);
            banks.put(hdfc.getId(), hdfc);
            banks.put(pnb.getId(),  pnb);

            // SBI — DOMESTIC FIXED
            // baseRate=10%, domesticDiscount=1%, femaleDiscount=0.5%
            // effectiveApplied for domestic male   = 10 - 1.0 = 9.0%
            // effectiveApplied for domestic female = 10 - 1.0 - 0.5 = 8.5%
            rates.put(RATE_SBI_DOM_FIXED, BankInterestRate.builder()
                    .id(RATE_SBI_DOM_FIXED)
                    .bank(sbi)
                    .loanType(LoanType.DOMESTIC)
                    .interestType(InterestType.FIXED)
                    .baseRate(new BigDecimal("10.00"))
                    .domesticDiscount(new BigDecimal("1.00"))
                    .femaleDiscount(new BigDecimal("0.50"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("100000"))    // ₹1 lakh
                    .maxLoanAmount(new BigDecimal("2000000"))   // ₹20 lakhs
                    .minTenureMonths(12)
                    .maxTenureMonths(180)                       // 15 years
                    .maxMoratoriumMonths(96)                    // study(max 84) + grace(max 12)
                    .minGracePeriodMonths(0)
                    .maxGracePeriodMonths(12)
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(null)                          // currently active
                    .isActive(true)
                    .build());

            // SBI — DOMESTIC FLOATING
            // baseRate=9.5% (lower than fixed since risk is on borrower)
            rates.put(RATE_SBI_DOM_FLOAT, BankInterestRate.builder()
                    .id(RATE_SBI_DOM_FLOAT)
                    .bank(sbi)
                    .loanType(LoanType.DOMESTIC)
                    .interestType(InterestType.FLOATING)
                    .baseRate(new BigDecimal("9.50"))
                    .domesticDiscount(new BigDecimal("1.00"))
                    .femaleDiscount(new BigDecimal("0.50"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("100000"))
                    .maxLoanAmount(new BigDecimal("2000000"))
                    .minTenureMonths(12)
                    .maxTenureMonths(180)
                    .maxMoratoriumMonths(96)
                    .minGracePeriodMonths(0)
                    .maxGracePeriodMonths(12)
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(null)
                    .isActive(true)
                    .build());

            // SBI — INTERNATIONAL FIXED
            // Higher base rate, no domesticDiscount, higher cap (₹1.5 Cr)
            rates.put(RATE_SBI_INTL_FIXED, BankInterestRate.builder()
                    .id(RATE_SBI_INTL_FIXED)
                    .bank(sbi)
                    .loanType(LoanType.INTERNATIONAL)
                    .interestType(InterestType.FIXED)
                    .baseRate(new BigDecimal("11.50"))
                    .domesticDiscount(null)                     // no domestic discount for intl loans
                    .femaleDiscount(new BigDecimal("0.50"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("200000"))    // ₹2 lakhs
                    .maxLoanAmount(new BigDecimal("15000000"))  // ₹1.5 Cr
                    .minTenureMonths(24)
                    .maxTenureMonths(180)
                    .maxMoratoriumMonths(108)                   // intl courses can be longer
                    .minGracePeriodMonths(6)
                    .maxGracePeriodMonths(12)
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(null)
                    .isActive(true)
                    .build());

            // SBI — INTERNATIONAL FLOATING
            rates.put(RATE_SBI_INTL_FLOAT, BankInterestRate.builder()
                    .id(RATE_SBI_INTL_FLOAT)
                    .bank(sbi)
                    .loanType(LoanType.INTERNATIONAL)
                    .interestType(InterestType.FLOATING)
                    .baseRate(new BigDecimal("11.00"))
                    .domesticDiscount(null)
                    .femaleDiscount(new BigDecimal("0.50"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("200000"))
                    .maxLoanAmount(new BigDecimal("15000000"))
                    .minTenureMonths(24)
                    .maxTenureMonths(180)
                    .maxMoratoriumMonths(108)
                    .minGracePeriodMonths(6)
                    .maxGracePeriodMonths(12)
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(null)
                    .isActive(true)
                    .build());

            // HDFC — DOMESTIC FIXED
            // HDFC is slightly more expensive than SBI but offers higher max
            rates.put(RATE_HDFC_DOM_FIXED, BankInterestRate.builder()
                    .id(RATE_HDFC_DOM_FIXED)
                    .bank(hdfc)
                    .loanType(LoanType.DOMESTIC)
                    .interestType(InterestType.FIXED)
                    .baseRate(new BigDecimal("10.50"))
                    .domesticDiscount(new BigDecimal("1.50"))
                    .femaleDiscount(new BigDecimal("0.50"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("200000"))    // HDFC min is ₹2L
                    .maxLoanAmount(new BigDecimal("1500000"))   // ₹15 lakhs
                    .minTenureMonths(12)
                    .maxTenureMonths(144)                       // HDFC max 12 years
                    .maxMoratoriumMonths(96)
                    .minGracePeriodMonths(0)
                    .maxGracePeriodMonths(6)
                    .effectiveFrom(LocalDate.of(2024, 3, 1))
                    .effectiveTo(null)
                    .isActive(true)
                    .build());

            // HDFC — INTERNATIONAL FIXED
            rates.put(RATE_HDFC_INTL_FIXED, BankInterestRate.builder()
                    .id(RATE_HDFC_INTL_FIXED)
                    .bank(hdfc)
                    .loanType(LoanType.INTERNATIONAL)
                    .interestType(InterestType.FIXED)
                    .baseRate(new BigDecimal("12.00"))
                    .domesticDiscount(null)
                    .femaleDiscount(new BigDecimal("0.50"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("500000"))    // ₹5 lakhs min for intl
                    .maxLoanAmount(new BigDecimal("10000000"))  // ₹1 Cr
                    .minTenureMonths(24)
                    .maxTenureMonths(180)
                    .maxMoratoriumMonths(108)
                    .minGracePeriodMonths(6)
                    .maxGracePeriodMonths(12)
                    .effectiveFrom(LocalDate.of(2024, 3, 1))
                    .effectiveTo(null)
                    .isActive(true)
                    .build());

            // PNB — DOMESTIC FIXED
            rates.put(RATE_PNB_DOM_FIXED, BankInterestRate.builder()
                    .id(RATE_PNB_DOM_FIXED)
                    .bank(pnb)
                    .loanType(LoanType.DOMESTIC)
                    .interestType(InterestType.FIXED)
                    .baseRate(new BigDecimal("10.25"))
                    .domesticDiscount(new BigDecimal("0.50"))
                    .femaleDiscount(new BigDecimal("0.25"))
                    .subsidyRate(new BigDecimal("4.00"))
                    .minLoanAmount(new BigDecimal("50000"))     // PNB allows smaller loans
                    .maxLoanAmount(new BigDecimal("2000000"))
                    .minTenureMonths(12)
                    .maxTenureMonths(180)
                    .maxMoratoriumMonths(96)
                    .minGracePeriodMonths(0)
                    .maxGracePeriodMonths(12)
                    .effectiveFrom(LocalDate.of(2024, 2, 1))
                    .effectiveTo(null)
                    .isActive(true)
                    .build());

            // Link rates back to bank objects
            sbi.setInterestRates(List.of(
                    rates.get(RATE_SBI_DOM_FIXED), rates.get(RATE_SBI_DOM_FLOAT),
                    rates.get(RATE_SBI_INTL_FIXED), rates.get(RATE_SBI_INTL_FLOAT)));
            hdfc.setInterestRates(List.of(
                    rates.get(RATE_HDFC_DOM_FIXED), rates.get(RATE_HDFC_INTL_FIXED)));
            pnb.setInterestRates(List.of(
                    rates.get(RATE_PNB_DOM_FIXED)));
        }

        // ── Query Methods ────────────────────────────

        public List<Bank> getAllActiveBanks() {
            return banks.values().stream()
                    .filter(Bank::getIsActive)
                    .toList();
        }

        public Optional<Bank> findBankById(String id) {
            return Optional.ofNullable(banks.get(id));
        }

        public List<BankInterestRate> getActiveRatesForBank(String bankId,
                                                            LoanType loanType,
                                                            InterestType interestType) {
            return rates.values().stream()
                    .filter(r -> r.getBank().getId().equals(bankId))
                    .filter(r -> Boolean.TRUE.equals(r.getIsActive()))
                    .filter(r -> r.getEffectiveTo() == null)
                    .filter(r -> loanType == null      || r.getLoanType() == loanType)
                    .filter(r -> interestType == null  || r.getInterestType() == interestType)
                    .toList();
        }

        public Optional<BankInterestRate> findRateById(String rateId) {
            return Optional.ofNullable(rates.get(rateId));
        }

        // ── Builder Helpers ──────────────────────────

        private Bank buildBank(String id, String name, String code,
                               String ifscPrefix, String logoUrl) {
            return Bank.builder()
                    .id(id)
                    .bankName(name)
                    .bankCode(code)
                    .ifscPrefix(ifscPrefix)
                    .logoUrl(logoUrl)
                    .isActive(true)
                    .interestRates(new ArrayList<>())
                    .build();
        }

      */


