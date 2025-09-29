package com.pichincha.accounts.application.port.output;

import com.pichincha.accounts.domain.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(UUID id);
    Optional<Client> findByClientId(String clientId);
    List<Client> findAll();
    void deleteById(UUID id);
    boolean existsByClientId(String clientId);
    boolean existsByIdentification(String identification);
}