package com.bank.loan.eligibility_service.service;

import com.bank.loan.eligibility_service.Calculator.FOIRCalculator;
import com.bank.loan.eligibility_service.Calculator.LTVCalculator;
import com.bank.loan.eligibility_service.Validator.CoApplicantValidator;
import com.bank.loan.eligibility_service.Validator.EducationValidator;
import com.bank.loan.eligibility_service.Validator.FinancialValidator;
import com.bank.loan.eligibility_service.Validator.StudentValidator;
import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.*;
import com.bank.loan.eligibility_service.enums.CollegeCategory;
import com.bank.loan.eligibility_service.enums.RiskCategory;

import com.bank.loan.eligibility_service.nationalityStrategy.NationalityEligibilityStrategy;
import com.bank.loan.eligibility_service.nationalityStrategy.NationalityStrategyFactory;
import com.bank.loan.eligibility_service.repository.LoanEligibilityRepository;
import com.bank.loan.eligibility_service.strategy.RiskStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligiblityServiceImpl implements EligibilityService {

    private static final double MAX_LOAN_PERCENTAGE = 0.9;

    private static final String APPROVED_MESSAGE = "Loan Approved";
    private static final String REJECTED_MESSAGE = "Loan Rejected";

    private final LoanEligibilityRepository repository;

    private final StudentValidator studentValidator;
    private final EducationValidator educationValidator;
    private final FinancialValidator financialValidator;
    private final CoApplicantValidator coApplicantValidator;

    private final FOIRCalculator foirCalculator;
    private final LTVCalculator ltvCalculator;

    private final RiskStrategyFactory riskFactory;

    private final NationalityStrategyFactory nationalityFactory;
    @Override
    public EligibilityResponseDTO checkEligibility(EligibilityRequestDTO request) {

        StudentDetails student = request.getStudentDetails();
        EducationDetails education = request.getEducationDetails();
        FinancialDetails financial = request.getFinancialDetails();
        CoApplicantDetails coApplicant = request.getCoApplicantDetails();

        studentValidator.validate(student);
        educationValidator.validate(education);
        financialValidator.validate(financial);
        coApplicantValidator.validate(coApplicant);

        double totalIncome = financial.getAnnualIncome();

        if (coApplicant != null && Boolean.TRUE.equals(coApplicant.getCoApplicationPresent())){
            totalIncome +=coApplicant.getCoApplicantIncome();
        }
             //because main eligibility is depend on repayment capacity
        FinancialDetails updatedFinancial = FinancialDetails.builder()
                .annualIncome(totalIncome)
                .existingEMI(financial.getExistingEMI())
                .courseFees(financial.getCourseFees())
                .requestedLoanAmount(financial.getRequestedLoanAmount())
                .creditScore(financial.getCreditScore())
                .build();

        double foir = foirCalculator.calculate(updatedFinancial);
        double ltv = ltvCalculator.calculate(updatedFinancial);
        double maxEligibleAmount = financial.getCourseFees() * MAX_LOAN_PERCENTAGE;

        updatedFinancial.setFoir(foir);
        updatedFinancial.setLtvRatio(ltv);
        updatedFinancial.setMaxEligibleAmount(maxEligibleAmount);

        CollegeCategory category =education.getCollegeCategory();
        RiskCategory risk = riskFactory.evaluate(updatedFinancial, foir,category);
        boolean eligible = false;
        switch (risk) {

            case LOW:
                eligible = foir <= 40 && ltv <= 80;
                break;

            case MEDIUM:
                eligible = foir <= 50 && ltv <= 90;
                break;

            case HIGH:
                eligible = foir <= 60 && ltv <= 95;
                break;
        }

        NationalityEligibilityStrategy nationalityStrategy =
                nationalityFactory.getStrategy(student.getNationality());
        nationalityStrategy.validate(student, updatedFinancial, coApplicant);

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updateAt = createdAt.plusMinutes(6);
        LocalDate decisionDate = createdAt.toLocalDate();

        LoanEligibility entity = LoanEligibility.builder()
                .studentDetails(student)
                .educationDetails(education)
                .financialDetails(updatedFinancial)
                .coApplicantDetails(coApplicant)
                .decisionDetails(
                        DecisionDetails.builder()
                                .eligible(eligible)
                                .riskCategory(risk)
                                .rejectionReason(eligible ? null : "FOIR/LTV exceeded")
                                .decisionDate(decisionDate)
                                .build()
                )
                .updatedAt(updateAt)
                .createdAt(createdAt)
                .build();

        LoanEligibility saved = repository.save(entity);

        return EligibilityResponseDTO.builder()
                .eligible(eligible)
                .riskCategory(risk.name())
                .foir(foir)
                .ltvRatio(ltv)
                .maxEligibleAmount(maxEligibleAmount)
                .message(eligible ? APPROVED_MESSAGE : REJECTED_MESSAGE)
                .build();
    }

    @Override
    public List<LoanEligibility> getAllEligibility() {
        return repository.findAll();
    }

    public LoanEligibility getEligibilityById(Long id){
        return repository.findById(id).orElseThrow(()->new RuntimeException("record not found"));
    }

    public void deleteEligibility(Long id){
        repository.deleteById(id);
    }

}