package com.pichincha.accounts.infrastructure.adapter.persistence;

import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import com.pichincha.accounts.infrastructure.adapter.persistence.repository.AccountJpaRepository;
import com.pichincha.accounts.infrastructure.mapper.AccountEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountPersistenceAdapterTest {

    @Mock
    private AccountJpaRepository accountJpaRepository;
    @Mock
    private AccountEntityMapper accountEntityMapper;

    @InjectMocks
    private AccountPersistenceAdapter adapter;

    private Account sampleDomain(UUID id) {
        Account a = new Account();
        a.setId(id);
        a.setAccountNumber("123");
        return a;
    }

    private AccountEntity sampleEntity(UUID id) {
        AccountEntity e = new AccountEntity();
        e.setId(id);
        e.setAccountNumber("123");
        return e;
    }

    @Test
    void shouldSaveAndMapWhenValidAccount() {
        UUID id = UUID.randomUUID();
        Account domain = sampleDomain(id);
        AccountEntity entity = sampleEntity(id);

        when(accountEntityMapper.toEntity(domain)).thenReturn(entity);
        when(accountJpaRepository.save(entity)).thenReturn(entity);
        when(accountEntityMapper.toDomain(entity)).thenReturn(domain);

        Account result = adapter.save(domain);

        assertThat(result).isSameAs(domain);
        verify(accountJpaRepository).save(entity);
    }

    @Test
    void shouldFindByIdAndMapWhenPresent() {
        UUID id = UUID.randomUUID();
        AccountEntity entity = sampleEntity(id);
        Account domain = sampleDomain(id);
        when(accountJpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(accountEntityMapper.toDomain(entity)).thenReturn(domain);

        Optional<Account> result = adapter.findById(id);
        assertThat(result).contains(domain);
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(accountJpaRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Account> result = adapter.findById(id);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindByAccountNumberAndMapWhenPresent() {
        String number = "ABC";
        UUID id = UUID.randomUUID();
        AccountEntity entity = sampleEntity(id);
        Account domain = sampleDomain(id);
        when(accountJpaRepository.findByAccountNumber(number)).thenReturn(Optional.of(entity));
        when(accountEntityMapper.toDomain(entity)).thenReturn(domain);

        Optional<Account> result = adapter.findByAccountNumber(number);
        assertThat(result).contains(domain);
    }

    @Test
    void shouldReturnEmptyWhenFindByAccountNumberNotFound() {
        when(accountJpaRepository.findByAccountNumber("X")).thenReturn(Optional.empty());
        Optional<Account> result = adapter.findByAccountNumber("X");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllAndMapList() {
        UUID id = UUID.randomUUID();
        AccountEntity entity = sampleEntity(id);
        Account domain = sampleDomain(id);
        when(accountJpaRepository.findAll()).thenReturn(List.of(entity));
        when(accountEntityMapper.toDomain(entity)).thenReturn(domain);

        List<Account> result = adapter.findAll();
        assertThat(result).hasSize(1).first().isSameAs(domain);
    }

    @Test
    void shouldFindByClientIdAndMapList() {
        UUID clientId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        AccountEntity entity = sampleEntity(id);
        Account domain = sampleDomain(id);
        when(accountJpaRepository.findByClientId(clientId)).thenReturn(List.of(entity));
        when(accountEntityMapper.toDomain(entity)).thenReturn(domain);

        List<Account> result = adapter.findByClientId(clientId);
        assertThat(result).hasSize(1).first().isSameAs(domain);
    }

    @Test
    void shouldDeleteByIdWhenCalled() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        verify(accountJpaRepository).deleteById(id);
    }

    @Test
    void shouldReturnTrueWhenExistsByAccountNumberIsTrue() {
        when(accountJpaRepository.existsByAccountNumber("N")).thenReturn(true);
        assertThat(adapter.existsByAccountNumber("N")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenExistsByAccountNumberIsFalse() {
        when(accountJpaRepository.existsByAccountNumber("N")).thenReturn(false);
        assertThat(adapter.existsByAccountNumber("N")).isFalse();
    }
}
