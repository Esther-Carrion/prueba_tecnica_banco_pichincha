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
@Slf4j
public class ClientPersistenceAdapter implements ClientRepository {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientEntityMapper clientEntityMapper;

    @Override
    public Client save(Client client) {
        log.debug("Saving client to database: {}", client.getIdentification());
        log.debug("Client domain object: name={}, clientId={}, state={}", 
                 client.getName(), client.getClientId(), client.getState());
        ClientEntity entity = clientEntityMapper.toEntity(client);
        log.debug("Mapped entity: name={}, clientId={}, state={}", 
                 entity.getName(), entity.getClientId(), entity.getState());
        
        ClientEntity savedEntity = clientJpaRepository.save(entity);
        return clientEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Client> findById(UUID id) {
        log.debug("Finding client by ID: {}", id);
        return clientJpaRepository.findById(id)
                .map(clientEntityMapper::toDomain);
    }

    @Override
    public Optional<Client> findByClientId(String clientId) {
        log.debug("Finding client by clientId: {}", clientId);
        return clientJpaRepository.findByClientId(clientId)
                .map(clientEntityMapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        log.debug("Finding all clients");
        return clientJpaRepository.findAll()
                .stream()
                .map(clientEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting client with ID: {}", id);
        clientJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByClientId(String clientId) {
        log.debug("Checking if client exists by clientId: {}", clientId);
        return clientJpaRepository.existsByClientId(clientId);
    }

    @Override
    public boolean existsByIdentification(String identification) {
        log.debug("Checking if client exists by identification: {}", identification);
        return clientJpaRepository.existsByIdentification(identification);
    }
}