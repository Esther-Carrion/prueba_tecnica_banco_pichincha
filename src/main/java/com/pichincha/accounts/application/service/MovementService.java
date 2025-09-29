package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.input.MovementInputPort;
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
@Transactional
public class MovementService implements MovementInputPort {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Override
    public Movement createMovement(Movement movement) {

        
        if (movement.getAccountId() == null) {
            throw new InvalidMovementException("El ID de la cuenta no puede ser nulo");
        }

        Account account = accountRepository.findById(movement.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + movement.getAccountId()));

        if (!account.getState()) {
            throw new AccountInactiveException("La cuenta no está activa");
        }

        if (movement.getMovementType() == null) {
            throw new InvalidMovementException("El tipo de movimiento no puede ser nulo");
        }

        if (movement.getValue() == null || movement.getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidMovementException("El valor del movimiento debe ser diferente de cero");
        }

        BigDecimal currentBalance = account.getCurrentBalance();
        BigDecimal newBalance;

        if (isDebitMovement(movement.getMovementType())) {
            BigDecimal debitAmount = movement.getValue().abs().negate();
            newBalance = currentBalance.add(debitAmount);
            
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException("Saldo no disponible");
            }
            
            movement.setValue(debitAmount);
        } else {
            BigDecimal creditAmount = movement.getValue().abs();
            newBalance = currentBalance.add(creditAmount);
            movement.setValue(creditAmount);
        }
        
        if (movement.getDate() == null) {
            movement.setDate(LocalDateTime.now());
        }
        
        movement.setBalance(newBalance);

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        Movement savedMovement = movementRepository.save(movement);

        
        return savedMovement;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movement> findById(UUID id) {

        return movementRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movement> findAll() {

        return movementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movement> findByAccountId(UUID accountId) {

        return movementRepository.findByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movement> findByAccountIdAndDateRange(UUID accountId, LocalDate startDate, LocalDate endDate) {

        return movementRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    @Override
    public Movement updateMovement(UUID id, Movement movement) {

        throw new InvalidMovementException("No se permite la modificación de movimientos por integridad financiera");
    }

    @Override
    public void deleteMovement(UUID id) {
        throw new InvalidMovementException("No se permite la eliminación de movimientos por integridad financiera");
    }
    private boolean isDebitMovement(MovementType movementType) {
        return movementType == MovementType.RETIRO || movementType == MovementType.TRANSFERENCIA_OUT;
    }
}

