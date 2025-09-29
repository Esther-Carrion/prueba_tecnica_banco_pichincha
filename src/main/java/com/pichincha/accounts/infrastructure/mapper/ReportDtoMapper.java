package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.Report;
import com.pichincha.infrastructure.adapter.rest.dto.AccountSummaryDto;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoDto;
import com.pichincha.infrastructure.adapter.rest.dto.ReportDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportDtoMapper {

    public ReportDto toDto(Report report) {
        ReportDto dto = new ReportDto(
                report.getClient().getId(),
                report.getClient().getName(),
                report.getStartDate(),
                report.getEndDate(),
                report.getAccountStatements().stream()
                        .map(as -> toAccountSummaryDto(as.getAccount(), as.getMovements(), as.getAccountTotalDebits(), as.getAccountTotalCredits()))
                        .collect(Collectors.toList()),
                report.getTotalDebits(),
                report.getTotalCredits()
        );
        return dto;
    }

    private AccountSummaryDto toAccountSummaryDto(Account account, List<Movement> movements, BigDecimal debitos, BigDecimal creditos) {
        AccountSummaryDto summary = new AccountSummaryDto(
                account.getAccountNumber() != null ? Long.valueOf(account.getAccountNumber()) : null,
                account.getType() != null ? AccountSummaryDto.TipoEnum.valueOf(account.getType().name()) : null,
                account.getInitialBalance(),
                account.getCurrentBalance(),
                debitos,
                creditos,
                movements.stream().map(this::toMovimientoDto).collect(Collectors.toList())
        );
        return summary;
    }

    private MovimientoDto toMovimientoDto(Movement movement) {
        return new MovimientoDto(
                movement.getId(),
                movement.getAccountId(),
                movement.getDate() != null ? movement.getDate().atOffset(ZoneOffset.UTC) : null,
                mapTipo(movement),
                movement.getValue(),
                movement.getBalance()
        );
    }

    private MovimientoDto.TipoEnum mapTipo(Movement movement) {
        if (movement.getValue() == null) return null;
        return movement.getValue().compareTo(BigDecimal.ZERO) >= 0 ? MovimientoDto.TipoEnum.CREDITO : MovimientoDto.TipoEnum.DEBITO;
    }
}
