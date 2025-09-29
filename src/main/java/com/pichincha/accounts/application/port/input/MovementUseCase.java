package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Movement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovementUseCase {
    Movement createMovement(Movement movement);
    Optional<Movement> findById(UUID id);
    List<Movement> findAll();
    List<Movement> findByAccountId(UUID accountId);
    List<Movement> findByAccountIdAndDateRange(UUID accountId, LocalDate startDate, LocalDate endDate);
    Movement updateMovement(UUID id, Movement movement);
    void deleteMovement(UUID id);
}