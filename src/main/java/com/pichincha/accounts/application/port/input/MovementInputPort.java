package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Movement;
import com.pichincha.infrastructure.adapter.rest.dto.ReportDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MovementInputPort {
    Movement createMovement(Movement movement);
    Movement updateMovement(UUID id, Movement movement);
    Movement getMovementById(UUID id);
    List<Movement> getAllMovements();
    List<Movement> getMovementsByAccountId(UUID accountId);
    List<Movement> getMovementsByClientAndDateRange(UUID clientId, LocalDate startDate, LocalDate endDate);
    byte[] generateReportPdf(UUID clientId, LocalDate startDate, LocalDate endDate);
    List<ReportDto> generateReportJson(UUID clientId, LocalDate startDate, LocalDate endDate);
}
