package com.loanmanagement.mapper;

import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.entity.LoanApplication;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationMapper {

    public LoanApplicationResponseDTO toResponse(LoanApplication a) {
        return new LoanApplicationResponseDTO(
                a.getId(),
                a.getApplicationNumber(),
                a.getBankId(),
                a.getBankName(),
                a.getBankInterestRateId(),
                a.getApplicantName(),
                a.getGender(),
                a.getLoanType(),
                a.getInterestType(),
                a.getCourseFee(),
                a.getLivingExpenses(),
                a.getTravelExpenses(),
                a.getBookEquipmentCost(),
                a.getInsuranceCoverage(),
                a.getLaptopAllowance(),
                a.getLoanAmount(),
                a.getBaseInterestRate(),
                a.getFloatingSpread(),
                a.getBenchmarkName(),
                a.getAppliedInterestRate(),
                a.getStudyPeriodMonths(),
                a.getGracePeriodMonths(),
                a.getMoratoriumPeriod(),
                a.getMoratoriumInterest(),
                a.getPrincipalAfterMoratorium(),
                a.getTotalRepayableAmount(),
                a.getSubsidized(),
                a.getSubsidyScheme(),
                a.getStatus(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
