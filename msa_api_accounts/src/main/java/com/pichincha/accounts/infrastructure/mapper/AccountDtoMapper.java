package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Account;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaDto;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountDtoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(source = "clienteId", target = "clientId")
    @Mapping(source = "tipo", target = "type")
    @Mapping(source = "saldoInicial", target = "initialBalance")
    @Mapping(source = "saldoInicial", target = "currentBalance")
    @Mapping(source = "estado", target = "state")
    @Mapping(target = "client", ignore = true)
    Account toEntity(CuentaCreateDto dto);
    
    @Mapping(source = "clienteId", target = "clientId")
    @Mapping(target = "accountNumber", expression = "java(dto.getNumeroCuenta() != null ? dto.getNumeroCuenta().toString() : null)")
    @Mapping(source = "tipo", target = "type")
    @Mapping(source = "saldoInicial", target = "initialBalance")
    @Mapping(source = "saldoActual", target = "currentBalance")
    @Mapping(source = "estado", target = "state")
    @Mapping(target = "client", ignore = true)
    Account toEntity(CuentaDto dto);
    
    @Mapping(target = "numeroCuenta", expression = "java(entity.getAccountNumber() != null ? Long.valueOf(entity.getAccountNumber()) : null)")
    @Mapping(source = "type", target = "tipo")
    @Mapping(source = "initialBalance", target = "saldoInicial")
    @Mapping(source = "currentBalance", target = "saldoActual")
    @Mapping(source = "state", target = "estado")
    @Mapping(source = "clientId", target = "clienteId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CuentaDto toDto(Account entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(source = "tipo", target = "type")
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(source = "estado", target = "state")
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    void updateEntity(@MappingTarget Account entity, CuentaUpdateDto dto);
}