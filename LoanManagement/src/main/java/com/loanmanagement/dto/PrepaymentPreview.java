package com.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class PrepaymentPreview {

    private BigDecimal outstanding;
    private BigDecimal charges;
    private BigDecimal totalPayable;
}
