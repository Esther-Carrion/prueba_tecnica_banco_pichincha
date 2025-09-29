package com.pichincha.accounts.infrastructure.adapter.persistence.repository;

import com.pichincha.accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    List<AccountEntity> findByClientId(UUID clientId);
    boolean existsByAccountNumber(String accountNumber);
}