package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.application.port.output.MovementRepository;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.enums.MovementType;
import com.pichincha.accounts.domain.exception.AccountInactiveException;
import com.pichincha.accounts.domain.exception.AccountNotFoundException;
import com.pichincha.accounts.domain.exception.InsufficientFundsException;
import com.pichincha.accounts.domain.exception.InvalidMovementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private MovementService movementService;

    private Account activeAccount(UUID id, BigDecimal balance) {
        Account a = new Account();
        a.setId(id);
        a.setCurrentBalance(balance);
        a.setState(true);
        return a;
    }

    @Test
    void shouldCreateCreditMovementAndIncreaseBalanceWhenValidRequest() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, new BigDecimal("50.00"))));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> {
            Movement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.DEPOSITO)
                .value(new BigDecimal("30.00"))
                .date(LocalDateTime.now())
                .build();

        Movement saved = movementService.createMovement(req);

        assertThat(saved.getValue()).isEqualByComparingTo("30.00");
        assertThat(saved.getBalance()).isEqualByComparingTo("80.00");
        verify(accountRepository).save(any(Account.class));
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void shouldCreateDebitMovementAndDecreaseBalanceWhenSufficientFunds() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, new BigDecimal("100.00"))));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> {
            Movement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.RETIRO)
                .value(new BigDecimal("30.00"))
                .build();

        Movement saved = movementService.createMovement(req);

        assertThat(saved.getValue()).isEqualByComparingTo("-30.00");
        assertThat(saved.getBalance()).isEqualByComparingTo("70.00");
        verify(accountRepository).save(any(Account.class));
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void shouldCreateTransferenciaOutDebitMovementWhenSufficientFunds() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, new BigDecimal("100.00"))));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> {
            Movement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.TRANSFERENCIA_OUT)
                .value(new BigDecimal("25.00"))
                .build();

        Movement saved = movementService.createMovement(req);

        assertThat(saved.getValue()).isEqualByComparingTo("-25.00");
        assertThat(saved.getBalance()).isEqualByComparingTo("75.00");
    }

    @Test
    void shouldSetCurrentDateWhenDateIsNull() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, new BigDecimal("50.00"))));
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> {
            Movement m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.DEPOSITO)
                .value(new BigDecimal("10.00"))
                .date(null)
                .build();

        Movement saved = movementService.createMovement(req);

        assertThat(saved.getDate()).isNotNull();
        assertThat(saved.getDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldThrowInvalidMovementExceptionWhenAccountIdIsNull() {
        Movement req = Movement.builder()
                .accountId(null)
                .movementType(MovementType.DEPOSITO)
                .value(BigDecimal.TEN)
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessageContaining("El ID de la cuenta no puede ser nulo");
    }

    @Test
    void shouldThrowInvalidMovementExceptionWhenMovementTypeIsNull() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, BigDecimal.TEN)));

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(null)
                .value(BigDecimal.TEN)
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessageContaining("El tipo de movimiento no puede ser nulo");
    }

    @Test
    void shouldThrowInvalidMovementExceptionWhenValueIsNull() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, BigDecimal.TEN)));

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.DEPOSITO)
                .value(null)
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessageContaining("El valor del movimiento debe ser diferente de cero");
    }

    @Test
    void shouldThrowInvalidMovementExceptionWhenValueIsZero() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, BigDecimal.TEN)));

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.DEPOSITO)
                .value(BigDecimal.ZERO)
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessageContaining("El valor del movimiento debe ser diferente de cero");
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenDebitExceedsBalance() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount(accountId, new BigDecimal("10.00"))));

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.RETIRO)
                .value(new BigDecimal("20.00"))
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Saldo no disponible");
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.DEPOSITO)
                .value(BigDecimal.ONE)
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldThrowAccountInactiveExceptionWhenAccountIsInactive() {
        UUID accountId = UUID.randomUUID();
        Account inactive = activeAccount(accountId, BigDecimal.ZERO);
        inactive.setState(false);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(inactive));

        Movement req = Movement.builder()
                .accountId(accountId)
                .movementType(MovementType.DEPOSITO)
                .value(BigDecimal.ONE)
                .build();

        assertThatThrownBy(() -> movementService.createMovement(req))
                .isInstanceOf(AccountInactiveException.class);
    }

    @Test
    void shouldReturnMovementWhenFindByIdExists() {
        UUID movementId = UUID.randomUUID();
        Movement movement = new Movement();
        movement.setId(movementId);
        when(movementRepository.findById(movementId)).thenReturn(Optional.of(movement));

        Optional<Movement> result = movementService.findById(movementId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(movementId);
        verify(movementRepository).findById(movementId);
    }

    @Test
    void shouldReturnEmptyOptionalWhenFindByIdNotExists() {
        UUID movementId = UUID.randomUUID();
        when(movementRepository.findById(movementId)).thenReturn(Optional.empty());

        Optional<Movement> result = movementService.findById(movementId);

        assertThat(result).isEmpty();
        verify(movementRepository).findById(movementId);
    }

    @Test
    void shouldReturnAllMovementsWhenFindAllCalled() {
        List<Movement> movements = List.of(new Movement(), new Movement());
        when(movementRepository.findAll()).thenReturn(movements);

        List<Movement> result = movementService.findAll();

        assertThat(result).hasSize(2);
        verify(movementRepository).findAll();
    }

    @Test
    void shouldReturnMovementsByAccountIdWhenFindByAccountIdCalled() {
        UUID accountId = UUID.randomUUID();
        List<Movement> movements = List.of(new Movement());
        when(movementRepository.findByAccountId(accountId)).thenReturn(movements);

        List<Movement> result = movementService.findByAccountId(accountId);

        assertThat(result).hasSize(1);
        verify(movementRepository).findByAccountId(accountId);
    }

    @Test
    void shouldReturnMovementsByAccountIdAndDateRangeWhenFindByAccountIdAndDateRangeCalled() {
        UUID accountId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Movement> movements = List.of(new Movement());
        when(movementRepository.findByAccountIdAndDateRange(accountId, startDate, endDate)).thenReturn(movements);

        List<Movement> result = movementService.findByAccountIdAndDateRange(accountId, startDate, endDate);

        assertThat(result).hasSize(1);
        verify(movementRepository).findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    @Test
    void shouldThrowInvalidMovementExceptionWhenUpdateMovementCalled() {
        assertThatThrownBy(() -> movementService.updateMovement(UUID.randomUUID(), new Movement()))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessageContaining("No se permite la modificación");
    }

    @Test
    void shouldThrowInvalidMovementExceptionWhenDeleteMovementCalled() {
        assertThatThrownBy(() -> movementService.deleteMovement(UUID.randomUUID()))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessageContaining("No se permite la eliminación");
    }
}
