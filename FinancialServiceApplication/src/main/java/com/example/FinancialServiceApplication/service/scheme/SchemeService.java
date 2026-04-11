package com.example.FinancialServiceApplication.service.scheme;

import com.example.FinancialServiceApplication.entity.LoanDetails;
import com.example.FinancialServiceApplication.entity.UserDetails;
import com.example.FinancialServiceApplication.exception.InvalidSchemeSelectionException;
import com.example.FinancialServiceApplication.exception.SchemeNotFoundException;
import com.example.FinancialServiceApplication.service.scheme.factory.SchemeFactory;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class SchemeService {
    private final SchemeFactory factory;
    public SchemeService(SchemeFactory factory) {
        this.factory = factory;
    }
    public List<SchemeResult> getEligibleSchemes(LoanDetails loan, UserDetails user) {
        List<SchemeResult> results = factory.getApplicable(loan, user)
                .stream()
                .map(s -> s.evaluate(loan, user))
                .toList();
        if (results.isEmpty()) {
            throw new SchemeNotFoundException("No eligible schemes found for this user");
        }
        return results;
    }
    public SchemeResult getRecommendedScheme(LoanDetails loan, UserDetails user) {
        List<SchemeResult> schemes = getEligibleSchemes(loan, user);
        return schemes.stream()
                .max((a, b) -> Double.compare(a.getBenefit(), b.getBenefit()))
                .orElseThrow(() ->
                        new SchemeNotFoundException("Unable to determine best scheme"));
    }
    public SchemeResult applySelectedScheme(LoanDetails loan,
                                            UserDetails user,
                                            String selectedScheme) {
        return factory.getApplicable(loan, user)
                .stream()
                .map(s -> s.evaluate(loan, user))
                .filter(result -> result.getSchemeName()
                        .equalsIgnoreCase(selectedScheme))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidSchemeSelectionException(selectedScheme));

    }
}