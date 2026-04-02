package com.loanmanagement.service.repayment;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.entity.Bank;
import com.loanmanagement.entity.BankInterestRate;
import com.loanmanagement.entity.InterestCalculationResult;
import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.enums.LoanStatus;
import com.loanmanagement.enums.LoanType;
import com.loanmanagement.exception.LoanBusinessException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.mapper.LoanApplicationMapper;
import com.loanmanagement.repository.LoanRepository;
import com.loanmanagement.util.LoanValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final MockDataStore mockDataStore;
    private final LoanRepository loanRepository;
    private final InterestCalculationService interestCalculationService;
    private final LoanApplicationMapper mapper;

    public LoanServiceImpl(MockDataStore mockDataStore, LoanRepository loanRepository,
                           InterestCalculationService interestCalculationService, LoanApplicationMapper mapper) {
        this.mockDataStore = mockDataStore;
        this.loanRepository = loanRepository;
        this.interestCalculationService = interestCalculationService;
        this.mapper = mapper;
    }


    @Override
    public LoanApplicationResponseDTO submitApplication(LoanApplicationRequestDTO requestDTO) {
        log.info("Starting loan application for applicant: {}", requestDTO.getApplicantName());

        Bank bank = fetchBank(requestDTO.getBankId());
        BankInterestRate rate = fetchRate(requestDTO.getBankInterestRateId());

        BigDecimal loanAmount = validateAndCalculateLoanAmount(requestDTO, rate);

        InterestCalculationResult calc = interestCalculationService.calculate(
                loanAmount, rate, requestDTO.getGender(),
                requestDTO.getStudyPeriodMonths(),
                requestDTO.getGracePeriodMonths(),
                requestDTO.getRequestedTenureMonths()
        );

        String appNumber = generateApplicationNumber(bank, requestDTO.getLoanType());

        LoanApplication application = LoanApplication.builder()
                .applicationNumber(appNumber)
                .bankId(bank.getId())
                .bankName(bank.getBankName())
                .bankInterestRateId(rate.getId())
                .applicantName(requestDTO.getApplicantName())
                .gender(requestDTO.getGender())
                .loanType(requestDTO.getLoanType())
                .interestType(requestDTO.getInterestType())
                // coverage buckets
                .courseFee(requestDTO.getCourseFee())
                .livingExpenses(requestDTO.getLivingExpenses())
                .travelExpenses(requestDTO.getTravelExpenses())
                .bookEquipmentCost(requestDTO.getBookEquipmentCost())
                .insuranceCoverage(requestDTO.getInsuranceCoverage())
                .laptopAllowance(requestDTO.getLaptopAllowance())
                .loanAmount(loanAmount)
                // interest snapshot
                .baseInterestRate(rate.getBaseRate())
                .floatingSpread(rate.getFloatingSpread())
                .benchmarkName(rate.getBenchmarkName())
                .appliedInterestRate(calc.appliedInterestRate())
                .studyPeriodMonths(requestDTO.getStudyPeriodMonths())
                .gracePeriodMonths(requestDTO.getGracePeriodMonths())
                .moratoriumPeriod(calc.moratoriumPeriod())
                .moratoriumInterest(calc.moratoriumInterest())
                .principalAfterMoratorium(calc.principalAfterMoratorium())
                .totalRepayableAmount(calc.totalRepayableAmount())
                // subsidy
                .subsidized(Boolean.TRUE.equals(requestDTO.getSubsidized()))
                .subsidyScheme(requestDTO.getSubsidyScheme())
                // lifecycle
                .status(LoanStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        loanRepository.save(application);
        log.info("Loan application saved successfully with ID: {}", application.getId());

        return mapper.toResponse(application);
    }


    @Override
    public LoanApplicationResponseDTO calculateLoan(LoanApplicationRequestDTO requestDTO) {
        Bank bank = fetchBank(requestDTO.getBankId());
        BankInterestRate rate = fetchRate(requestDTO.getBankInterestRateId());

        BigDecimal loanAmount = validateAndCalculateLoanAmount(requestDTO, rate);

        InterestCalculationResult calc = interestCalculationService.calculate(
                loanAmount, rate, requestDTO.getGender(),
                requestDTO.getStudyPeriodMonths(),
                requestDTO.getGracePeriodMonths(),
                requestDTO.getRequestedTenureMonths()
        );

        String appNumber = generateApplicationNumber(bank, requestDTO.getLoanType());

        return mapper.toResponse(
                LoanApplication.builder()
                        .id(UUID.randomUUID().toString())
                        .applicationNumber(appNumber)
                        .bankId(bank.getId())
                        .bankName(bank.getBankName())
                        .bankInterestRateId(rate.getId())
                        .applicantName(requestDTO.getApplicantName())
                        .gender(requestDTO.getGender())
                        .loanType(requestDTO.getLoanType())
                        .interestType(requestDTO.getInterestType())
                        .courseFee(requestDTO.getCourseFee())
                        .livingExpenses(requestDTO.getLivingExpenses())
                        .travelExpenses(requestDTO.getTravelExpenses())
                        .bookEquipmentCost(requestDTO.getBookEquipmentCost())
                        .insuranceCoverage(requestDTO.getInsuranceCoverage())
                        .laptopAllowance(requestDTO.getLaptopAllowance())
                        .loanAmount(loanAmount)
                        .baseInterestRate(rate.getBaseRate())
                        .floatingSpread(rate.getFloatingSpread())
                        .benchmarkName(rate.getBenchmarkName())
                        .appliedInterestRate(calc.appliedInterestRate())
                        .studyPeriodMonths(requestDTO.getStudyPeriodMonths())
                        .gracePeriodMonths(requestDTO.getGracePeriodMonths())
                        .moratoriumPeriod(calc.moratoriumPeriod())
                        .moratoriumInterest(calc.moratoriumInterest())
                        .principalAfterMoratorium(calc.principalAfterMoratorium())
                        .totalRepayableAmount(calc.totalRepayableAmount())
                        .subsidized(requestDTO.getSubsidized())
                        .subsidyScheme(requestDTO.getSubsidyScheme())
                        .status(null) // not submitted
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    private Bank fetchBank(String bankId) {
        return mockDataStore.findBankById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found: " + bankId));
    }

    private BankInterestRate fetchRate(String rateId) {
        return mockDataStore.findRateById(rateId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate not found: " + rateId));
    }

    private BigDecimal validateAndCalculateLoanAmount(LoanApplicationRequestDTO requestDTO, BankInterestRate rate) {
        LoanValidationUtils.validateRateLoanTypeMatch(rate, requestDTO);
        BigDecimal loanAmount = LoanValidationUtils.sumCoverageBuckets(requestDTO);
        LoanValidationUtils.validateLoanAmountRange(loanAmount, rate);
        LoanValidationUtils.validateTenure(requestDTO.getRequestedTenureMonths(), rate);
        LoanValidationUtils.validateGracePeriod(requestDTO.getGracePeriodMonths(), rate);
        LoanValidationUtils.validateMoratorium(requestDTO.getStudyPeriodMonths(),
                requestDTO.getGracePeriodMonths(), rate);
        return loanAmount;
    }

    private String generateApplicationNumber(Bank bank, LoanType loanType) {
        int year = LocalDateTime.now().getYear();
        long count = loanRepository.countApplicationsForYear(bank.getId(), year);
        long sequence = count + 1;
        return bank.getBankCode()
                + "-" + loanType.name().substring(0, 3)
                + "-" + year
                + String.format("%04d", sequence);
    }

}