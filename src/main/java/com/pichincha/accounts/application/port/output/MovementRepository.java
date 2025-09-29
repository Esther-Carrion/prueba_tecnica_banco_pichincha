package com.pichincha.accounts.application.port.output;

import com.pichincha.accounts.domain.Movement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovementRepository {
    Movement save(Movement movement);
    Optional<Movement> findById(UUID id);
    List<Movement> findAll();
    List<Movement> findByAccountId(UUID accountId);
    List<Movement> findByAccountIdAndDateRange(UUID accountId, LocalDate startDate, LocalDate endDate);
    void deleteById(UUID id);
}