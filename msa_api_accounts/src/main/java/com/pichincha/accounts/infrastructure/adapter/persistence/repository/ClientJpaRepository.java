package com.pichincha.accounts.infrastructure.adapter.persistence.repository;

import com.pichincha.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {
    Optional<ClientEntity> findByClientId(String clientId);
    boolean existsByClientId(String clientId);
    boolean existsByIdentification(String identification);
}