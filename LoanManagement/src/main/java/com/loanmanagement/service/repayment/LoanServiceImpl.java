package com.loanmanagement.service.repayment;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.dto.LoanCalculationResponseDTO;
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
    private final LoanValidationUtils loanValidationUtils;

    public LoanServiceImpl(MockDataStore mockDataStore, LoanRepository loanRepository, InterestCalculationService interestCalculationService, LoanApplicationMapper mapper, LoanValidationUtils loanValidationUtils) {
        this.mockDataStore = mockDataStore;
        this.loanRepository = loanRepository;
        this.interestCalculationService = interestCalculationService;
        this.mapper = mapper;
        this.loanValidationUtils = loanValidationUtils;
    }

    @Override
    public LoanApplicationResponseDTO submitApplication(LoanApplicationRequestDTO requestDTO) {
        log.info("Starting loan application for applicant: {}", requestDTO.getApplicantName());

        boolean exists = loanRepository.existsByApplicantNameAndBankId(requestDTO.getApplicantName(),
                requestDTO.getBankId());
        if (exists) {
            log.error("Duplicate application found for applicant: {} and bankId: {}",requestDTO.getApplicantName(), requestDTO.getBankId());
            throw new LoanBusinessException("Loan application already exists for this applicant and bank.");
        }
        log.debug("Fetching bank and interest rate details");
        Bank bank = fetchBank(requestDTO.getBankId());
        BankInterestRate rate = fetchRate(requestDTO.getBankInterestRateId());

        BigDecimal loanAmount = loanValidationUtils.validateAndCalculateLoanAmount(requestDTO, rate);
        log.debug("Calculated loan amount: {}", loanAmount);

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

                .subsidized(Boolean.TRUE.equals(requestDTO.getSubsidized()))
                .subsidyScheme(requestDTO.getSubsidyScheme())

                .status(LoanStatus.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        loanRepository.save(application);
        log.info("Loan application saved successfully with applicationNumber: {}", application.getApplicationNumber());
        return mapper.toResponse(application);
    }

    @Override
    public LoanCalculationResponseDTO calculateLoan(LoanApplicationRequestDTO requestDTO) {
        log.info("Calculating loan for applicant: {}", requestDTO.getApplicantName());

        Bank bank = fetchBank(requestDTO.getBankId());
        BankInterestRate rate = fetchRate(requestDTO.getBankInterestRateId());

        BigDecimal loanAmount = loanValidationUtils.validateAndCalculateLoanAmount(requestDTO, rate);

        InterestCalculationResult calc = interestCalculationService.calculate(
                loanAmount, rate, requestDTO.getGender(),
                requestDTO.getStudyPeriodMonths(),
                requestDTO.getGracePeriodMonths(),
                requestDTO.getRequestedTenureMonths()
        );
        log.info("Loan calculation completed for applicant: {}", requestDTO.getApplicantName());
        return new LoanCalculationResponseDTO(
                loanAmount,
                calc.appliedInterestRate(),
                calc.moratoriumPeriod(),
                calc.moratoriumInterest(),
                calc.principalAfterMoratorium(),
                calc.totalRepayableAmount()
        );
    }

    @Override
    public LoanApplicationResponseDTO getLoanByApplicationNumber(String applicationNumber) {

        log.info("Fetching loan for applicationNumber: {}", applicationNumber);

        LoanApplication loanApplication = loanRepository
                .findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> {log.error("Loan not found for applicationNumber: {}", applicationNumber);
                    return new ResourceNotFoundException("Loan not found with applicationNumber: " + applicationNumber);
                });

        log.info("Loan fetched successfully for applicationNumber: {}", applicationNumber);
        return mapper.toResponse(loanApplication);

    }

    private Bank fetchBank(String bankId) {
        log.debug("Fetching bank with ID: {}", bankId);
        Bank bank =  mockDataStore.findBankById(bankId)
                .orElseThrow(() -> {log.error("Bank not found: {}", bankId);
           return new ResourceNotFoundException("Bank not found: " + bankId)
                   ;});

        if (!Boolean.TRUE.equals(bank.getIsActive())) {
            log.error("Bank is inactive: {}", bankId);
            throw new LoanBusinessException("Bank is not active: " + bankId);
        }

        return bank;
    }

    private BankInterestRate fetchRate(String rateId) {
        log.debug("Fetching interest rate with ID: {}", rateId);
        BankInterestRate rate = mockDataStore.findRateById(rateId)
                .orElseThrow(() -> {log.error("Interest rate not found: {}", rateId);
                    return new ResourceNotFoundException("Rate not found: " + rateId);});

        if (!Boolean.TRUE.equals(rate.getIsActive())) {
            log.error("Interest rate inactive: {}", rateId);
            throw new LoanBusinessException("Interest rate is not active: " + rateId);
        }
        return rate;

    }

    private String generateApplicationNumber(Bank bank, LoanType loanType) {
        int year = LocalDateTime.now().getYear();
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String appNumber =  bank.getBankCode()
                + "-" + loanType.name().substring(0, 3)
                + "-" + year
                + "-" + random;
        log.debug("Generated application number: {}", appNumber);
        return appNumber;
    }

}