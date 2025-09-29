package com.pichincha.accounts.infrastructure.adapter.persistence;

import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import com.pichincha.accounts.infrastructure.adapter.persistence.repository.AccountJpaRepository;
import com.pichincha.accounts.infrastructure.mapper.AccountEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AccountPersistenceAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountEntityMapper accountEntityMapper;

    @Override
    public Account save(Account account) {
        log.debug("Saving account to database: {}", account.getAccountNumber());
        // Importante: NO forzar id a null; si viene con id, JPA hará update.
        AccountEntity entity = accountEntityMapper.toEntity(account);
        AccountEntity savedEntity = accountJpaRepository.save(entity);
        return accountEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        log.debug("Finding account by ID: {}", id);
        return accountJpaRepository.findById(id)
                .map(accountEntityMapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        log.debug("Finding account by number: {}", accountNumber);
        return accountJpaRepository.findByAccountNumber(accountNumber)
                .map(accountEntityMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        log.debug("Finding all accounts");
        return accountJpaRepository.findAll()
                .stream()
                .map(accountEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findByClientId(UUID clientId) {
        log.debug("Finding accounts by client ID: {}", clientId);
        return accountJpaRepository.findByClientId(clientId)
                .stream()
                .map(accountEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting account with ID: {}", id);
        accountJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        log.debug("Checking if account exists by number: {}", accountNumber);
        return accountJpaRepository.existsByAccountNumber(accountNumber);
    }
}