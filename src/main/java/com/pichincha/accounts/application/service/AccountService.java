package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.input.AccountUseCase;
import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.exception.AccountNotFoundException;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pichincha.accounts.util.NumberGenerate.generateAccountNumber;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService implements AccountUseCase {
    
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Override
    public Account createAccount(Account account) {
        log.info("Creating new account for client ID: {}", account.getClientId());

        Client client = clientRepository.findById(account.getClientId())
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + account.getClientId()));

        if (!client.getState()) {
            throw new RuntimeException("No se puede crear cuenta para un cliente inactivo");
        }

        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        account.setAccountNumber(accountNumber);

        if (account.getState() == null) {
            account.setState(true);
        }

        account.setCurrentBalance(account.getInitialBalance());
        
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with number: {}", savedAccount.getAccountNumber());
        return savedAccount;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(UUID id) {
        log.debug("Finding account by ID: {}", id);
        return accountRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByAccountNumber(String accountNumber) {
        log.debug("Finding account by number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        log.debug("Finding all accounts");
        return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByClientId(UUID clientId) {
        log.debug("Finding accounts by client ID: {}", clientId);
        return accountRepository.findByClientId(clientId);
    }

    @Override
    public Account updateAccount(UUID id, Account account) {
        log.info("Updating account with ID: {}", id);
        
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + id));

        if (account.getType() != null) {
            existingAccount.setType(account.getType());
        }
        if (account.getState() != null) {
            existingAccount.setState(account.getState());
        }
        
        Account updatedAccount = accountRepository.save(existingAccount);
        log.info("Account updated successfully with ID: {}", updatedAccount.getId());
        return updatedAccount;
    }

    @Override
    public void deleteAccount(UUID id) {
        log.info("Deleting account with ID: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + id));

        if (account.getCurrentBalance().compareTo(java.math.BigDecimal.ZERO) != 0) {
            throw new RuntimeException("No se puede eliminar una cuenta con saldo diferente a cero");
        }
        
        accountRepository.deleteById(id);
        log.info("Account deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
}
