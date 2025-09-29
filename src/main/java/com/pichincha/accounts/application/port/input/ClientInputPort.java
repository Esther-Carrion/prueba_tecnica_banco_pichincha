package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Client;

import java.util.List;
import java.util.UUID;

public interface ClientInputPort {
    Client createClient(Client client);
    Client updateClient(UUID id, Client client);
    Client getClientById(UUID id);
    List<Client> getAllClients();
    void deleteClient(UUID id);
}
