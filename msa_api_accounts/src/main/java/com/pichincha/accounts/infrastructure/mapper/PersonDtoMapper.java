package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Person;
import com.pichincha.infrastructure.adapter.rest.dto.PersonaDto;
import com.pichincha.infrastructure.adapter.rest.dto.PersonaCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.PersonaUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonDtoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "genero", target = "gender")
    @Mapping(target = "age", ignore = true)
    @Mapping(source = "identificacion", target = "identification")
    @Mapping(source = "telefono", target = "phone")
    @Mapping(source = "direccion", target = "address")
    Person toEntity(PersonaCreateDto dto);
    
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "genero", target = "gender")
    @Mapping(target = "age", ignore = true)
    @Mapping(source = "identificacion", target = "identification")
    @Mapping(source = "telefono", target = "phone")
    @Mapping(source = "direccion", target = "address")
    Person toEntity(PersonaDto dto);
    
    @Mapping(source = "name", target = "nombre")
    @Mapping(target = "apellido", ignore = true)
    @Mapping(source = "gender", target = "genero")
    @Mapping(target = "fechaNacimiento", ignore = true)
    @Mapping(source = "identification", target = "identificacion")
    @Mapping(source = "phone", target = "telefono")
    @Mapping(source = "address", target = "direccion")
    @Mapping(target = "estado", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PersonaDto toDto(Person entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "genero", target = "gender")
    @Mapping(target = "age", ignore = true)
    @Mapping(source = "identificacion", target = "identification")
    @Mapping(source = "telefono", target = "phone")
    @Mapping(source = "direccion", target = "address")
    void updateEntity(@MappingTarget Person entity, PersonaUpdateDto dto);
}