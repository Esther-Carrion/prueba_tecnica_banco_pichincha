package com.pichincha.accounts.infrastructure.adapter.persistence;

import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.MovementEntity;
import com.pichincha.accounts.infrastructure.adapter.persistence.repository.MovementJpaRepository;
import com.pichincha.accounts.infrastructure.mapper.MovementEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementPersistenceAdapterTest {

    @Mock
    private MovementJpaRepository movementJpaRepository;
    @Mock
    private MovementEntityMapper movementEntityMapper;

    @InjectMocks
    private MovementPersistenceAdapter adapter;

    private Movement domain(UUID id) {
        Movement m = new Movement();
        m.setId(id);
        return m;
    }

    private MovementEntity entity(UUID id) {
        MovementEntity e = new MovementEntity();
        e.setId(id);
        return e;
    }

    @Test
    void shouldSaveAndMapWhenValidMovement() {
        UUID id = UUID.randomUUID();
        Movement d = domain(id);
        MovementEntity e = entity(id);
        when(movementEntityMapper.toEntity(d)).thenReturn(e);
        when(movementJpaRepository.save(e)).thenReturn(e);
        when(movementEntityMapper.toDomain(e)).thenReturn(d);

        Movement result = adapter.save(d);
        assertThat(result).isSameAs(d);
        verify(movementJpaRepository).save(e);
    }

    @Test
    void shouldFindByIdAndMapWhenPresent() {
        UUID id = UUID.randomUUID();
        MovementEntity e = entity(id);
        Movement d = domain(id);
        when(movementJpaRepository.findById(id)).thenReturn(Optional.of(e));
        when(movementEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findById(id)).contains(d);
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(movementJpaRepository.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void shouldFindAllAndMapList() {
        MovementEntity e = entity(UUID.randomUUID());
        Movement d = domain(e.getId());
        when(movementJpaRepository.findAll()).thenReturn(List.of(e));
        when(movementEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findAll()).hasSize(1).first().isSameAs(d);
    }

    @Test
    void shouldFindByAccountIdAndMapList() {
        UUID accountId = UUID.randomUUID();
        MovementEntity e = entity(UUID.randomUUID());
        Movement d = domain(e.getId());
        when(movementJpaRepository.findByAccountIdOrderByDateDesc(accountId)).thenReturn(List.of(e));
        when(movementEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findByAccountId(accountId)).hasSize(1).first().isSameAs(d);
    }

    @Test
    void shouldFindByAccountIdAndDateRangeAndMapList() {
        UUID accountId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        MovementEntity e = entity(UUID.randomUUID());
        Movement d = domain(e.getId());
        when(movementJpaRepository.findByAccountIdAndDateBetweenOrderByDateDesc(eq(accountId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(e));
        when(movementEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findByAccountIdAndDateRange(accountId, start, end)).hasSize(1).first().isSameAs(d);
    }

    @Test
    void shouldDeleteByIdWhenCalled() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        verify(movementJpaRepository).deleteById(id);
    }
}
