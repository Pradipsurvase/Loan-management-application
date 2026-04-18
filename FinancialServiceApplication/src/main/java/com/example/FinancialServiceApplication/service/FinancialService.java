package com.example.FinancialServiceApplication.service;

import com.example.FinancialServiceApplication.client.LoanClient;
import com.example.FinancialServiceApplication.client.UserClient;
import com.example.FinancialServiceApplication.dto.*;
import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.exception.LoanNotFoundException;
import com.example.FinancialServiceApplication.exception.UserNotFoundException;
import com.example.FinancialServiceApplication.service.charges.*;
import com.example.FinancialServiceApplication.service.scheme.*;
import com.example.FinancialServiceApplication.validation.FinancialValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FinancialService {

    private final SchemeService schemeService;
    private final ChargesService chargesService;
    private final LoanClient loanClient;
    private final UserClient userClient;
    private final FinancialValidator validator;

    public FinancialService(SchemeService schemeService,
                            ChargesService chargesService,
                            LoanClient loanClient,
                            UserClient userClient,
                            FinancialValidator validator)
    {
        this.schemeService = schemeService;
        this.chargesService = chargesService;
        this.loanClient = loanClient;
        this.userClient = userClient;
        this.validator = validator;
    }

    public SchemeOptionsResponse getSchemeOptions(SchemeRequest request)
    {
        log.info("Fetching schemes for loanId={}", request.getLoanId());

        LoanDetails loan = loanClient.getLoan(request.getLoanId());
        if (loan == null) {
            throw new LoanNotFoundException(request.getLoanId());
        }

        UserDetails user = userClient.getUser(loan.getUserId());
        if (user == null) {
            throw new UserNotFoundException(loan.getUserId());
        }

        validator.validate(loan);

        List<SchemeResult> eligibleSchemes =
                schemeService.getEligibleSchemes(loan, user);

        SchemeResult best =
                schemeService.getRecommendedScheme(loan, user);

        return SchemeOptionsResponse.builder()
                .eligibleSchemes(eligibleSchemes)
                .recommendedScheme(best.getSchemeName())
                .build();
    }

    public FinancialResponse process(ProcessRequest request)
    {
        log.info("Processing loanId={}, bank={}, scheme={}",
                request.getLoanId(),
                request.getBankName(),
                request.getSelectedScheme());

        LoanDetails loan = loanClient.getLoan(request.getLoanId());
        if (loan == null) {
            throw new LoanNotFoundException(request.getLoanId());
        }

        UserDetails user = userClient.getUser(loan.getUserId());
        if (user == null) {
            throw new UserNotFoundException(loan.getUserId());
        }

        validator.validate(loan);

        SchemeResult schemeResult =
                schemeService.applySelectedScheme(
                        loan,
                        user,
                        request.getSelectedScheme()
                );


        try {
            LoanSchemeUpdateRequest dto =
                    LoanSchemeUpdateRequest.builder()
                            .loanId(request.getLoanId())
                            .schemeName(schemeResult.getSchemeName())
                            .subsidyAmount(schemeResult.getBenefit())
                            .build();

            loanClient.sendSchemeData(dto);

            log.info("Scheme data sent to loan-service for loanId={}", request.getLoanId());

        } catch (Exception e) {
            log.warn("Failed to send scheme data to loan-service: {}", e.getMessage());
        }


        LoanDetails updatedLoan = loan.toBuilder()
                .amount(schemeResult.getUpdatedLoanAmount())
                .build();

        ChargeResult chargeResult =
                chargesService.applyCharges(updatedLoan, request.getBankName());

        log.info("Final payable calculated for loanId={}", request.getLoanId());

        return FinancialResponse.builder()
                .originalLoan(loan.getAmount())
                .updatedLoan(updatedLoan.getAmount())
                .subsidy(schemeResult.getBenefit())
                .charges(chargeResult.getTotalCharges())
                .finalPayable(updatedLoan.getAmount() + chargeResult.getTotalCharges())
                .build();
    }
}