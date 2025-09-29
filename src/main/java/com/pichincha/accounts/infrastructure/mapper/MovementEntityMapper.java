package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovementEntityMapper {
    
    MovementEntity toEntity(Movement movement);
    
    Movement toDomain(MovementEntity entity);
}