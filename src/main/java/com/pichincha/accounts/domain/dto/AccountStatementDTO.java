package com.pichincha.accounts.domain.dto;

import com.pichincha.accounts.domain.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountStatementDTO {
    private LocalDateTime fecha;
    private String cliente;
    private String numeroCuenta;
    private AccountType tipo;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private BigDecimal movimiento;
    private BigDecimal saldoDisponible;
}
