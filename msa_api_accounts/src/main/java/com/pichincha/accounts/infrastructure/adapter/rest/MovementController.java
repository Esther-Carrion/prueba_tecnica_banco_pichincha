package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.MovementInputPort;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.exception.InvalidMovementException;
import com.pichincha.accounts.infrastructure.mapper.MovementDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.MovimientoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor

@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class MovementController {

    private final MovementInputPort movementInputPort;
    private final MovementDtoMapper movementDtoMapper;

    @PostMapping
    public ResponseEntity<MovimientoDto> createMovement(@Valid @RequestBody MovimientoCreateDto dto) {
        Movement movement = movementDtoMapper.toDomain(dto);

        try {
            Movement createdMovement = movementInputPort.createMovement(movement);
            return ResponseEntity.status(HttpStatus.CREATED).body(movementDtoMapper.toDto(createdMovement));
        } catch (RuntimeException e) {

            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDto> getMovementById(@PathVariable UUID id) {

        Optional<Movement> movement = movementInputPort.findById(id);
        return movement.map(m -> ResponseEntity.ok(movementDtoMapper.toDto(m)))
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDto>> getAllMovements() {

        List<MovimientoDto> movements = movementInputPort.findAll().stream()
                .map(movementDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<MovimientoDto>> getMovementsByAccountId(@PathVariable UUID accountId) {

        List<MovimientoDto> movements = movementInputPort.findByAccountId(accountId).stream()
                .map(movementDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/account/{accountId}/dateRange")
    public ResponseEntity<List<MovimientoDto>> getMovementsByAccountIdAndDateRange(
            @PathVariable UUID accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {


        List<MovimientoDto> movements = movementInputPort.findByAccountIdAndDateRange(accountId, startDate, endDate).stream()
                .map(movementDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(movements);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateMovement(@PathVariable UUID id, @Valid @RequestBody MovimientoCreateDto dto) {

        try {
            movementInputPort.updateMovement(id, movementDtoMapper.toDomain(dto));
            return ResponseEntity.badRequest()
                    .body("No se permite la modificación de movimientos por integridad financiera");
        } catch (InvalidMovementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovement(@PathVariable UUID id) {

        try {
            movementInputPort.deleteMovement(id);
            return ResponseEntity.badRequest()
                    .body("No se permite la eliminación de movimientos por integridad financiera");
        } catch (InvalidMovementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}