package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountUseCase {
    Account createAccount(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    List<Account> findByClientId(UUID clientId);
    Account updateAccount(UUID id, Account account);
    void deleteAccount(UUID id);
    boolean existsByAccountNumber(String accountNumber);
}