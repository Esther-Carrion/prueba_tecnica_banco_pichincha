package com.pichincha.accounts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private LocalDate startDate;
    private LocalDate endDate;
    private Client client;
    private List<AccountStatement> accountStatements;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private BigDecimal totalBalance;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountStatement {
        private Account account;
        private List<Movement> movements;
        private BigDecimal accountTotalCredits;
        private BigDecimal accountTotalDebits;
        private BigDecimal finalBalance;
    }
}