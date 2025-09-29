package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.input.MovementUseCase;
import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.application.port.output.MovementRepository;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.enums.MovementType;
import com.pichincha.accounts.domain.exception.AccountInactiveException;
import com.pichincha.accounts.domain.exception.AccountNotFoundException;
import com.pichincha.accounts.domain.exception.InsufficientFundsException;
import com.pichincha.accounts.domain.exception.InvalidMovementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovementService implements MovementUseCase {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Override
    public Movement createMovement(Movement movement) {
        log.info("Creating new movement for account ID: {}", movement.getAccountId());
        
        if (movement.getAccountId() == null) {
            throw new InvalidMovementException("El ID de la cuenta no puede ser nulo");
        }

        Account account = accountRepository.findById(movement.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + movement.getAccountId()));

        // Validar que la cuenta esté activa
        if (!account.getState()) {
            throw new AccountInactiveException("La cuenta no está activa");
        }

        // Validar el tipo de movimiento
        if (movement.getMovementType() == null) {
            throw new InvalidMovementException("El tipo de movimiento no puede ser nulo");
        }

        // Validar el valor del movimiento
        if (movement.getValue() == null || movement.getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidMovementException("El valor del movimiento debe ser diferente de cero");
        }

        BigDecimal currentBalance = account.getCurrentBalance();
        BigDecimal newBalance;

        // Procesar según el tipo de movimiento
        if (isDebitMovement(movement.getMovementType())) {
            // Para débitos, el valor debe ser negativo y verificar saldo suficiente
            BigDecimal debitAmount = movement.getValue().abs().negate();
            newBalance = currentBalance.add(debitAmount);
            
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException("Saldo no disponible");
            }
            
            movement.setValue(debitAmount); // Asegurar que el débito sea negativo
        } else {
            // Para créditos, el valor debe ser positivo
            BigDecimal creditAmount = movement.getValue().abs();
            newBalance = currentBalance.add(creditAmount);
            movement.setValue(creditAmount); // Asegurar que el crédito sea positivo
        }

        // JPA generará automáticamente el ID con @GeneratedValue - no establecer manualmente
        
        if (movement.getDate() == null) {
            movement.setDate(LocalDateTime.now());
        }
        
        movement.setBalance(newBalance);

        // Actualizar el saldo de la cuenta
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        // Guardar el movimiento
        Movement savedMovement = movementRepository.save(movement);
        log.info("Movement created successfully with ID: {}, new balance: {}", 
                savedMovement.getId(), newBalance);
        
        return savedMovement;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movement> findById(UUID id) {
        log.debug("Finding movement by ID: {}", id);
        return movementRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movement> findAll() {
        log.debug("Finding all movements");
        return movementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movement> findByAccountId(UUID accountId) {
        log.debug("Finding movements by account ID: {}", accountId);
        return movementRepository.findByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movement> findByAccountIdAndDateRange(UUID accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding movements by account ID: {} and date range: {} to {}", 
                accountId, startDate, endDate);
        return movementRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    @Override
    public Movement updateMovement(UUID id, Movement movement) {
        log.info("Updating movement with ID: {}", id);
        
        // No permitir actualizaciones de movimientos por integridad financiera
        throw new InvalidMovementException("No se permite la modificación de movimientos por integridad financiera");
    }

    @Override
    public void deleteMovement(UUID id) {
        log.info("Deleting movement with ID: {}", id);
        
        // No permitir eliminaciones de movimientos por integridad financiera
        throw new InvalidMovementException("No se permite la eliminación de movimientos por integridad financiera");
    }

    /**
     * Determina si un tipo de movimiento es un débito
     */
    private boolean isDebitMovement(MovementType movementType) {
        return movementType == MovementType.RETIRO || 
               movementType == MovementType.TRANSFERENCIA_OUT;
    }
}
/*

        if ("DEBITO".equalsIgnoreCase(movement.getMovementType()) || movement.getValue().compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal debitAmount = movement.getValue().abs();
            if (currentBalance.compareTo(debitAmount) < 0) {
                throw new InsufficientFundsException("Saldo no disponible");
            }
            newBalance = currentBalance.subtract(debitAmount);
            movement.setValue(debitAmount.negate());
        } else {
            newBalance = currentBalance.add(movement.getValue());
        }

        movement.setBalanceBefore(currentBalance);
        movement.setBalanceAfter(newBalance);
        movement.setBalance(newBalance);
        movement.setDate(LocalDateTime.now());
        movement.setAccount(account);

        account.setCurrentBalance(newBalance);
        accountOutputPort.save(account);

        return movementOutputPort.save(movement);
    }

    @Override
    public Movement updateMovement(UUID id, Movement movement) {
        Movement existing = movementOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found with ID: " + id));
        
        if (movement.getMovementType() != null) {
            existing.setMovementType(movement.getMovementType());
        }
        
        return movementOutputPort.save(existing);
    }

    @Override
    public Movement getMovementById(UUID id) {
        return movementOutputPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found with ID: " + id));
    }

    @Override
    public List<Movement> getAllMovements() {
        return movementOutputPort.findAll();
    }

    @Override
    public List<Movement> getMovementsByAccountId(UUID accountId) {
        return movementOutputPort.findByAccountId(accountId);
    }

    @Override
    public List<Movement> getMovementsByClientAndDateRange(UUID clientId, LocalDate startDate, LocalDate endDate) {
        List<Account> accounts = accountOutputPort.findByClientId(clientId);
        
        return accounts.stream()
                .flatMap(account -> movementOutputPort.findByAccountIdAndDateRange(account.getId(), startDate, endDate).stream())
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateReportPdf(UUID clientId, LocalDate startDate, LocalDate endDate) {
        Client client = clientService.getClientById(clientId);
        List<Movement> movements = getMovementsByClientAndDateRange(clientId, startDate, endDate);
        
        return reportService.generatePdfReport(movements, client, startDate, endDate);
    }

    @Override
    public List<ReportDto> generateReportJson(UUID clientId, LocalDate startDate, LocalDate endDate) {
        List<Movement> movements = getMovementsByClientAndDateRange(clientId, startDate, endDate);
        
        return movements.stream()
                .map(movementDtoMapper::toReportDto)
                .collect(Collectors.toList());
    }
}

 */