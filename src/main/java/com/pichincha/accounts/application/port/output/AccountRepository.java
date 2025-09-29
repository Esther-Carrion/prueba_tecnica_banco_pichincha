package com.pichincha.accounts.application.port.output;

import com.pichincha.accounts.domain.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    List<Account> findByClientId(UUID clientId);
    void deleteById(UUID id);
    boolean existsByAccountNumber(String accountNumber);
}