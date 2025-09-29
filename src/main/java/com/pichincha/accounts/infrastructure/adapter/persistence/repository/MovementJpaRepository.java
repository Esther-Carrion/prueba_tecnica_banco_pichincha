package com.pichincha.accounts.infrastructure.adapter.persistence.repository;

import com.pichincha.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovementJpaRepository extends JpaRepository<MovementEntity, UUID> {
    List<MovementEntity> findByAccountIdOrderByDateDesc(UUID accountId);
    List<MovementEntity> findByAccountIdAndDateBetweenOrderByDateDesc(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}