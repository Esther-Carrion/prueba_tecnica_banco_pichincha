package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountEntityMapper {
    
    AccountEntity toEntity(Account account);
    
    Account toDomain(AccountEntity entity);
}