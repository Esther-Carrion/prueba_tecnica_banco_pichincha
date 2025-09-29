package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Account;

import java.util.List;
import java.util.UUID;

public interface AccountInputPort {
    Account createAccount(Account account);
    Account updateAccount(UUID id, Account account);
    Account getAccountById(UUID id);
    List<Account> getAllAccounts();
    List<Account> getAccountsByClientId(UUID clientId);
    void deleteAccount(UUID id);
}
