package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.MovementInputPort;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.exception.InvalidMovementException;
import com.pichincha.accounts.infrastructure.mapper.MovementDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementControllerTest {

    @Mock
    private MovementInputPort movementInputPort;
    @Mock
    private MovementDtoMapper movementDtoMapper;

    @InjectMocks
    private MovementController controller;

    private Movement domain(UUID id) {
        Movement m = new Movement();
        m.setId(id);
        return m;
    }

    @Test
    void shouldCreateMovementAndReturnCreatedWhenValidRequest() {
        MovimientoCreateDto createDto = new MovimientoCreateDto();
        Movement d = domain(UUID.randomUUID());
        MovimientoDto dto = new MovimientoDto();
        when(movementDtoMapper.toDomain(createDto)).thenReturn(d);
        when(movementInputPort.createMovement(d)).thenReturn(d);
        when(movementDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<MovimientoDto> response = controller.createMovement(createDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnOkWhenGetMovementByIdExists() {
        UUID id = UUID.randomUUID();
        Movement d = domain(id);
        MovimientoDto dto = new MovimientoDto();
        when(movementInputPort.findById(id)).thenReturn(Optional.of(d));
        when(movementDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<MovimientoDto> response = controller.getMovementById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenGetMovementByIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(movementInputPort.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<MovimientoDto> response = controller.getMovementById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnAllMovementsWhenGetAllCalled() {
        Movement d = domain(UUID.randomUUID());
        MovimientoDto dto = new MovimientoDto();
        when(movementInputPort.findAll()).thenReturn(List.of(d));
        when(movementDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<List<MovimientoDto>> response = controller.getAllMovements();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnMovementsByAccountIdWhenGetByAccountIdCalled() {
        UUID accountId = UUID.randomUUID();
        Movement d = domain(UUID.randomUUID());
        MovimientoDto dto = new MovimientoDto();
        when(movementInputPort.findByAccountId(accountId)).thenReturn(List.of(d));
        when(movementDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<List<MovimientoDto>> response = controller.getMovementsByAccountId(accountId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnMovementsByAccountIdAndDateRangeWhenCalled() {
        UUID accountId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        Movement d = domain(UUID.randomUUID());
        MovimientoDto dto = new MovimientoDto();
        when(movementInputPort.findByAccountIdAndDateRange(accountId, start, end)).thenReturn(List.of(d));
        when(movementDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<List<MovimientoDto>> response = controller.getMovementsByAccountIdAndDateRange(accountId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnBadRequestMessageWhenUpdateMovementAttempted() {
        UUID id = UUID.randomUUID();
        MovimientoCreateDto dto = new MovimientoCreateDto();
        doThrow(new InvalidMovementException("No se permite la modificación de movimientos por integridad financiera"))
                .when(movementInputPort).updateMovement(any(), any());
        ResponseEntity<String> response = controller.updateMovement(id, dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("No se permite la modificación");
    }

    @Test
    void shouldReturnBadRequestMessageWhenDeleteMovementAttempted() {
        UUID id = UUID.randomUUID();
        doThrow(new InvalidMovementException("No se permite la eliminación de movimientos por integridad financiera"))
                .when(movementInputPort).deleteMovement(id);
        ResponseEntity<String> response = controller.deleteMovement(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("No se permite la eliminación");
    }
}
