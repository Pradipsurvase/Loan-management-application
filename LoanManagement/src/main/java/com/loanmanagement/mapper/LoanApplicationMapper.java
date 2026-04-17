package com.loanmanagement.mapper;

import com.loanmanagement.dto.LoanApplicationResponseDTO;
import com.loanmanagement.entity.LoanApplication;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationMapper {

    public LoanApplicationResponseDTO toResponse(LoanApplication loan) {

        LoanApplicationResponseDTO response = new LoanApplicationResponseDTO();

        response.setLoanId(loan.getLoanId());
        response.setApplicationNumber(loan.getApplicationNumber());
        response.setStatus(loan.getStatus() != null ? loan.getStatus().name() : null);


        response.setApplicant(new LoanApplicationResponseDTO.Applicant(
                loan.getApplicantName(),
                loan.getGender().name()
        ));

        response.setBank(new LoanApplicationResponseDTO.Bank(
                loan.getBankId(),
                loan.getBankName(),
                loan.getBankInterestRateId()
        ));

        response.setLoanDetails(new LoanApplicationResponseDTO.LoanDetails(
                loan.getLoanType().name(),
                loan.getInterestType().name(),
                loan.getBaseInterestRate(),
                loan.getAppliedInterestRate(),
                loan.getLoanAmount()
        ));

        response.setExpenses(new LoanApplicationResponseDTO.Expenses(
                loan.getCourseFee(),
                loan.getLivingExpenses(),
                loan.getTravelExpenses(),
                loan.getBookEquipmentCost(),
                loan.getInsuranceCoverage(),
                loan.getLaptopAllowance()
        ));

        LoanApplicationResponseDTO.Repayment repayment = new LoanApplicationResponseDTO.Repayment();
        repayment.setStudyPeriodMonths(loan.getStudyPeriodMonths());
        repayment.setGracePeriodMonths(loan.getGracePeriodMonths());
        repayment.setMoratoriumPeriodMonths(loan.getMoratoriumPeriod());
        repayment.setMoratoriumInterest(loan.getMoratoriumInterest());
        repayment.setPrincipalAfterMoratorium(loan.getPrincipalAfterMoratorium());
        repayment.setTotalRepayableAmount(loan.getTotalRepayableAmount());

        response.setRepayment(repayment);

        response.setSubsidy(new LoanApplicationResponseDTO.Subsidy(
                Boolean.TRUE.equals(loan.getSubsidized()),
                loan.getSubsidyScheme()
        ));

        response.setTimestamps(new LoanApplicationResponseDTO.Timestamps(
                loan.getCreatedAt(),
                loan.getUpdatedAt()
        ));

        return response;
    }
}
