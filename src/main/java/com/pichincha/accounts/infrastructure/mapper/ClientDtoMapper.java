package com.pichincha.accounts.infrastructure.mapper;

import com.pichincha.accounts.domain.Client;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteDto;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteUpdateDto;
import com.pichincha.infrastructure.adapter.rest.dto.PersonaCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.PersonaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientDtoMapper {

    // Create DTO -> Domain
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "persona.nombre", target = "name")
    @Mapping(source = "persona.genero", target = "gender")
    @Mapping(target = "age", ignore = true)
    @Mapping(source = "persona.identificacion", target = "identification")
    @Mapping(source = "persona.telefono", target = "phone")
    @Mapping(source = "persona.direccion", target = "address")
    @Mapping(source = "username", target = "clientId")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "estado", target = "state")
    Client toDomain(ClienteCreateDto dto);

    // Read DTO -> Domain
    @Mapping(source = "id", target = "id")
    @Mapping(source = "persona.nombre", target = "name")
    @Mapping(source = "persona.genero", target = "gender")
    @Mapping(target = "age", ignore = true)
    @Mapping(source = "persona.identificacion", target = "identification")
    @Mapping(source = "persona.telefono", target = "phone")
    @Mapping(source = "persona.direccion", target = "address")
    @Mapping(source = "username", target = "clientId")
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "estado", target = "state")
    Client toDomain(ClienteDto dto);

    // Domain -> DTO
    @Mapping(source = "id", target = "id")
    @Mapping(target = "personaId", ignore = true)
    @Mapping(source = "clientId", target = "username")
    @Mapping(source = "state", target = "estado")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "persona", expression = "java(toPersonaDto(entity))")
    ClienteDto toDto(Client entity);

    // Update DTO -> Domain (partial)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "persona.nombre", target = "name")
    @Mapping(source = "persona.genero", target = "gender")
    @Mapping(target = "age", ignore = true)
    @Mapping(source = "persona.identificacion", target = "identification")
    @Mapping(source = "persona.telefono", target = "phone")
    @Mapping(source = "persona.direccion", target = "address")
    @Mapping(source = "username", target = "clientId")
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "estado", target = "state")
    void updateDomain(@MappingTarget Client entity, ClienteUpdateDto dto);

    // Helper to map Person fields
    default PersonaDto toPersonaDto(Client entity) {
        if (entity == null) return null;
        PersonaDto persona = new PersonaDto(entity.getId(), entity.getName(), entity.getState() != null ? entity.getState() : Boolean.TRUE);
        persona.setGenero(entity.getGender() != null ? PersonaDto.GeneroEnum.valueOf(entity.getGender().name()) : null);
        persona.setIdentificacion(entity.getIdentification());
        persona.setTelefono(entity.getPhone());
        persona.setDireccion(entity.getAddress());
        return persona;
    }
}