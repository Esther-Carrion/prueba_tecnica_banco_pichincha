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

public class AccountPersistenceAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountEntityMapper accountEntityMapper;

    @Override
    public Account save(Account account) {

        AccountEntity entity = accountEntityMapper.toEntity(account);
        AccountEntity savedEntity = accountJpaRepository.save(entity);
        return accountEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {

        return accountJpaRepository.findById(id)
                .map(accountEntityMapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {

        return accountJpaRepository.findByAccountNumber(accountNumber)
                .map(accountEntityMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {

        return accountJpaRepository.findAll()
                .stream()
                .map(accountEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findByClientId(UUID clientId) {

        return accountJpaRepository.findByClientId(clientId)
                .stream()
                .map(accountEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {

        accountJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {

        return accountJpaRepository.existsByAccountNumber(accountNumber);
    }
}