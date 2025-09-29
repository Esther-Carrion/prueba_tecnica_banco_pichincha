package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientUseCase {
    Client createClient(Client client);
    Optional<Client> findById(UUID id);
    Optional<Client> findByClientId(String clientId);
    List<Client> findAll();
    Client updateClient(UUID id, Client client);
    void deleteClient(UUID id);
    boolean existsByClientId(String clientId);
    boolean existsByIdentification(String identification);
}