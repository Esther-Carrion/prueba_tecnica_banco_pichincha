package com.pichincha.accounts.infrastructure.adapter.persistence;

import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.infrastructure.adapter.persistence.entity.ClientEntity;
import com.pichincha.accounts.infrastructure.adapter.persistence.repository.ClientJpaRepository;
import com.pichincha.accounts.infrastructure.mapper.ClientEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor

public class ClientPersistenceAdapter implements ClientRepository {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientEntityMapper clientEntityMapper;

    @Override
    public Client save(Client client) {

        ClientEntity entity = clientEntityMapper.toEntity(client);
        ClientEntity savedEntity = clientJpaRepository.save(entity);
        return clientEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Client> findById(UUID id) {

        return clientJpaRepository.findById(id)
                .map(clientEntityMapper::toDomain);
    }

    @Override
    public Optional<Client> findByClientId(String clientId) {

        return clientJpaRepository.findByClientId(clientId)
                .map(clientEntityMapper::toDomain);
    }

    @Override
    public List<Client> findAll() {

        return clientJpaRepository.findAll()
                .stream()
                .map(clientEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        clientJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByClientId(String clientId) {
        return clientJpaRepository.existsByClientId(clientId);
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return clientJpaRepository.existsByIdentification(identification);
    }
}