package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.enums.AccountType;
import com.pichincha.accounts.domain.exception.AccountNotFoundException;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AccountService accountService;

    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
    }

    private Client activeClient() {
        Client c = new Client();
        c.setId(clientId);
        c.setClientId("C-123");
        c.setState(true);
        return c;
    }

    @Test
    void shouldCreateAccountAndSetNumberAndInitializeBalanceAndStateWhenClientIsActive() {
        Account toCreate = Account.builder()
                .clientId(clientId)
                .type(AccountType.AHORROS)
                .initialBalance(new BigDecimal("100.00"))
                .build();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(activeClient()));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account a = invocation.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        Account saved = accountService.createAccount(toCreate);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAccountNumber()).isNotBlank();
        assertThat(saved.getCurrentBalance()).isEqualByComparingTo("100.00");
        assertThat(saved.getState()).isTrue();

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
        Account toCreate = Account.builder()
                .clientId(clientId)
                .initialBalance(BigDecimal.TEN)
                .build();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createAccount(toCreate))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void shouldThrowRuntimeWhenClientIsInactive() {
    Account toCreate = Account.builder()
        .clientId(clientId)
        .initialBalance(new BigDecimal("50.00"))
        .build();

    Client inactive = new Client();
    inactive.setId(clientId);
    inactive.setClientId("C-123");
    inactive.setState(false);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(inactive));

    assertThatThrownBy(() -> accountService.createAccount(toCreate))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("cliente inactivo");
    }

    @Test
    void shouldKeepProvidedStateWhenStateIsProvidedFalse() {
    Account toCreate = Account.builder()
        .clientId(clientId)
        .type(AccountType.AHORROS)
        .initialBalance(new BigDecimal("10.00"))
        .state(false)
        .build();

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(activeClient()));
    when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
    when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Account saved = accountService.createAccount(toCreate);
    assertThat(saved.getState()).isFalse();
    }

    @Test
    void shouldThrowWhenDeletingAccountWhenBalanceIsNotZero() {
        UUID accountId = UUID.randomUUID();
        Account existing = Account.builder()
                .id(accountId)
                .clientId(clientId)
                .currentBalance(new BigDecimal("5.00"))
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> accountService.deleteAccount(accountId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("saldo diferente a cero");
    }

    @Test
    void shouldDeleteAccount_whenBalanceIsZero() {
        UUID accountId = UUID.randomUUID();
        Account existing = Account.builder()
                .id(accountId)
                .clientId(clientId)
                .currentBalance(BigDecimal.ZERO)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existing));

        accountService.deleteAccount(accountId);

        verify(accountRepository).deleteById(accountId);
    }

    @Test
    void shouldThrowAccountNotFoundWhenDeleteAccountDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.deleteAccount(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Cuenta no encontrada");
    }

    @Test
    void shouldUpdateTypeAndStateWhenAccountExists() {
        UUID accountId = UUID.randomUUID();
        Account existing = Account.builder()
                .id(accountId)
                .type(AccountType.AHORROS)
                .state(true)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account request = Account.builder()
                .type(AccountType.CORRIENTE)
                .state(false)
                .build();

        Account updated = accountService.updateAccount(accountId, request);

        assertThat(updated.getType()).isEqualTo(AccountType.CORRIENTE);
        assertThat(updated.getState()).isFalse();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowAccountNotFoundWhenAccountDoesNotExistOnUpdate() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.updateAccount(id, new Account()))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Cuenta no encontrada");
    }

    @Test
    void shouldDelegateFindAllWhenInvoked() {
        when(accountRepository.findAll()).thenReturn(List.of());
        assertThat(accountService.findAll()).isEmpty();
        verify(accountRepository).findAll();
    }

    @Test
    void shouldReturnAccountByAccountNumberOptionalWhenPresent() {
        String number = "ACC-42";
        Account existing = Account.builder().id(UUID.randomUUID()).accountNumber(number).build();
        when(accountRepository.findByAccountNumber(number)).thenReturn(Optional.of(existing));

        Optional<Account> result = accountService.findByAccountNumber(number);
        assertThat(result).isPresent();
        assertThat(result.get().getAccountNumber()).isEqualTo(number);
        verify(accountRepository).findByAccountNumber(number);
    }

    @Test
    void shouldReturnEmptyOptionalWhenFindByAccountNumberNotExists() {
        String number = "ACC-404";
        when(accountRepository.findByAccountNumber(number)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.findByAccountNumber(number);
        assertThat(result).isEmpty();
        verify(accountRepository).findByAccountNumber(number);
    }

    @Test
    void shouldDelegateFindByClientIdWhenInvoked() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findByClientId(id)).thenReturn(List.of());
        assertThat(accountService.findByClientId(id)).isEmpty();
        verify(accountRepository).findByClientId(id);
    }

    @Test
    void shouldReturnAccountOptionalWhenFindByIdExists() {
        UUID id = UUID.randomUUID();
        Account existing = Account.builder().id(id).build();
        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));

        Optional<Account> result = accountService.findById(id);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(accountRepository).findById(id);
    }

    @Test
    void shouldReturnEmptyOptionalWhenFindByIdNotExists() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.findById(id);
        assertThat(result).isEmpty();
        verify(accountRepository).findById(id);
    }

    @Test
    void shouldReturnTrueWhenExistsByAccountNumberInRepositoryIsTrue() {
        when(accountRepository.existsByAccountNumber("ACC-1")).thenReturn(true);

        boolean exists = accountService.existsByAccountNumber("ACC-1");
        assertThat(exists).isTrue();
        verify(accountRepository).existsByAccountNumber("ACC-1");
    }

    @Test
    void shouldReturnFalseWhenExistsByAccountNumberInRepositoryIsFalse() {
        when(accountRepository.existsByAccountNumber("ACC-2")).thenReturn(false);

        boolean exists = accountService.existsByAccountNumber("ACC-2");
        assertThat(exists).isFalse();
        verify(accountRepository).existsByAccountNumber("ACC-2");
    }
}
