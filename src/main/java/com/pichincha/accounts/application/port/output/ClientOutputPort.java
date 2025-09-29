package com.pichincha.accounts.application.port.output;

import com.pichincha.accounts.domain.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientOutputPort {
    Client save(Client client);
    Optional<Client> findById(UUID id);
    List<Client> findAll();
    void deleteById(UUID id);
    boolean existsByUsername(String username);
}
