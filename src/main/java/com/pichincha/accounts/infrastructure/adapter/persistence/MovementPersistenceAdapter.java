package com.pichincha.accounts.infrastructure.adapter.persistence;

import com.pichincha.accounts.application.port.output.MovementRepository;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import com.pichincha.accounts.infrastructure.adapter.persistence.repository.MovementJpaRepository;
import com.pichincha.accounts.infrastructure.mapper.MovementEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor

public class MovementPersistenceAdapter implements MovementRepository {

    private final MovementJpaRepository movementJpaRepository;
    private final MovementEntityMapper movementEntityMapper;

    @Override
    public Movement save(Movement movement) {
        MovementEntity entity = movementEntityMapper.toEntity(movement);
        MovementEntity savedEntity = movementJpaRepository.save(entity);
        return movementEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Movement> findById(UUID id) {

        return movementJpaRepository.findById(id)
                .map(movementEntityMapper::toDomain);
    }

    @Override
    public List<Movement> findAll() {

        return movementJpaRepository.findAll()
                .stream()
                .map(movementEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByAccountId(UUID accountId) {

        return movementJpaRepository.findByAccountIdOrderByDateDesc(accountId)
                .stream()
                .map(movementEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByAccountIdAndDateRange(UUID accountId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return movementJpaRepository.findByAccountIdAndDateBetweenOrderByDateDesc(accountId, startDateTime, endDateTime)
                .stream()
                .map(movementEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {

        movementJpaRepository.deleteById(id);
    }
}