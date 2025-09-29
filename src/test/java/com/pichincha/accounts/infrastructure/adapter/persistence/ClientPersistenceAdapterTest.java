package com.pichincha.accounts.infrastructure.adapter.persistence;

import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import com.pichincha.accounts.infrastructure.adapter.persistence.repository.ClientJpaRepository;
import com.pichincha.accounts.infrastructure.mapper.ClientEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientPersistenceAdapterTest {

    @Mock
    private ClientJpaRepository clientJpaRepository;
    @Mock
    private ClientEntityMapper clientEntityMapper;

    @InjectMocks
    private ClientPersistenceAdapter adapter;

    private Client domain(UUID id) {
        Client c = new Client();
        c.setId(id);
        c.setClientId("C1");
        c.setName("Ana");
        return c;
    }

    private ClientEntity entity(UUID id) {
        ClientEntity e = new ClientEntity();
        e.setId(id);
        e.setClientId("C1");
        e.setName("Ana");
        return e;
    }

    @Test
    void shouldSaveAndMapWhenValidClient() {
        UUID id = UUID.randomUUID();
        Client d = domain(id);
        ClientEntity e = entity(id);
        when(clientEntityMapper.toEntity(d)).thenReturn(e);
        when(clientJpaRepository.save(e)).thenReturn(e);
        when(clientEntityMapper.toDomain(e)).thenReturn(d);

        Client result = adapter.save(d);
        assertThat(result).isSameAs(d);
        verify(clientJpaRepository).save(e);
    }

    @Test
    void shouldFindByIdAndMapWhenPresent() {
        UUID id = UUID.randomUUID();
        ClientEntity e = entity(id);
        Client d = domain(id);
        when(clientJpaRepository.findById(id)).thenReturn(Optional.of(e));
        when(clientEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findById(id)).contains(d);
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(clientJpaRepository.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void shouldFindByClientIdAndMapWhenPresent() {
        ClientEntity e = entity(UUID.randomUUID());
        Client d = domain(e.getId());
        when(clientJpaRepository.findByClientId("C1")).thenReturn(Optional.of(e));
        when(clientEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findByClientId("C1")).contains(d);
    }

    @Test
    void shouldReturnEmptyWhenFindByClientIdNotFound() {
        when(clientJpaRepository.findByClientId("X")).thenReturn(Optional.empty());
        assertThat(adapter.findByClientId("X")).isEmpty();
    }

    @Test
    void shouldFindAllAndMapList() {
        ClientEntity e = entity(UUID.randomUUID());
        Client d = domain(e.getId());
        when(clientJpaRepository.findAll()).thenReturn(List.of(e));
        when(clientEntityMapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findAll()).hasSize(1).first().isSameAs(d);
    }

    @Test
    void shouldDeleteByIdWhenCalled() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        verify(clientJpaRepository).deleteById(id);
    }

    @Test
    void shouldReturnTrueWhenExistsByClientIdIsTrue() {
        when(clientJpaRepository.existsByClientId("C1")).thenReturn(true);
        assertThat(adapter.existsByClientId("C1")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenExistsByClientIdIsFalse() {
        when(clientJpaRepository.existsByClientId("C2")).thenReturn(false);
        assertThat(adapter.existsByClientId("C2")).isFalse();
    }

    @Test
    void shouldReturnTrueWhenExistsByIdentificationIsTrue() {
        when(clientJpaRepository.existsByIdentification("0102030405")).thenReturn(true);
        assertThat(adapter.existsByIdentification("0102030405")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenExistsByIdentificationIsFalse() {
        when(clientJpaRepository.existsByIdentification("0102030406")).thenReturn(false);
        assertThat(adapter.existsByIdentification("0102030406")).isFalse();
    }
}
