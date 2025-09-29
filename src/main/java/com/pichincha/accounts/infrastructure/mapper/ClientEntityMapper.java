package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientEntityMapper {
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "state", target = "state")
    ClientEntity toEntity(Client client);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "identification", target = "identification")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "state", target = "state")
    Client toDomain(ClientEntity entity);
}