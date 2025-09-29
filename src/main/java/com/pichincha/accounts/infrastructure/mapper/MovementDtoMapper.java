package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.enums.MovementType;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovementDtoMapper {

    // ---- CreateDto -> Domain ----
    default Movement toDomain(MovimientoCreateDto dto) {
        if (dto == null) return null;
        Movement movement = new Movement();
        movement.setAccountId(dto.getCuentaId());
        movement.setMovementType(mapTipoEnumToDomain(dto.getTipo()));
        movement.setValue(dto.getValor());
        movement.setDate(LocalDateTime.now());
        return movement;
    }

    // ---- Domain -> Dto ----
    default MovimientoDto toDto(Movement movement) {
        if (movement == null) return null;
    MovimientoDto dto = new MovimientoDto(
                movement.getId(),
                movement.getAccountId(),
                toOffsetDateTime(movement.getDate()),
        mapDomainToTipoEnumDto(movement.getMovementType()),
                sanitizeValue(movement.getValue()),
                sanitizeValue(movement.getBalance())
        );
        return dto;
    }

    // ---- Helpers ----
    private static MovimientoCreateDto.TipoEnum mapDomainToTipoEnum(MovementType type) {
        if (type == null) return null;
        return switch (type) {
            case RETIRO, TRANSFERENCIA_OUT -> MovimientoCreateDto.TipoEnum.DEBITO;
            case DEPOSITO, TRANSFERENCIA_IN -> MovimientoCreateDto.TipoEnum.CREDITO;
        };
    }

    private static MovementType mapTipoEnumToDomain(MovimientoCreateDto.TipoEnum tipo) {
        if (tipo == null) return null;
        return switch (tipo) {
            case DEBITO -> MovementType.RETIRO;
            case CREDITO -> MovementType.DEPOSITO;
        };
    }

    private static MovimientoDto.TipoEnum mapDomainToTipoEnumDto(MovementType type) {
        if (type == null) return null;
        return switch (type) {
            case RETIRO, TRANSFERENCIA_OUT -> MovimientoDto.TipoEnum.DEBITO;
            case DEPOSITO, TRANSFERENCIA_IN -> MovimientoDto.TipoEnum.CREDITO;
        };
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(ZoneOffset.UTC);
    }

    private static BigDecimal sanitizeValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}