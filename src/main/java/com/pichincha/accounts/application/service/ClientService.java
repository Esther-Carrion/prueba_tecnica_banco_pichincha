package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.input.ClientUseCase;
import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import com.pichincha.accounts.util.NumberGenerate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService implements ClientUseCase {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(Client client) {
        log.info("Creating new client with identification: {}", client.getIdentification());

        if (clientRepository.existsByIdentification(client.getIdentification())) {
            throw new RuntimeException("Ya existe un cliente con la identificación: " + client.getIdentification());
        }

        if (client.getClientId() == null || client.getClientId().isEmpty()) {
            String clientId;
            do {
                clientId = NumberGenerate.generateClientId();
            } while (clientRepository.existsByClientId(clientId));
            client.setClientId(clientId);
        }

        if (clientRepository.existsByClientId(client.getClientId())) {
            throw new RuntimeException("Ya existe un cliente con el ID: " + client.getClientId());
        }

        if (client.getState() == null) {
            client.setState(true);
        }
        
        Client savedClient = clientRepository.save(client);
        log.info("Client created successfully with ID: {}", savedClient.getId());
        return savedClient;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findById(UUID id) {
        log.debug("Finding client by ID: {}", id);
        return clientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findByClientId(String clientId) {
        log.debug("Finding client by clientId: {}", clientId);
        return clientRepository.findByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        log.debug("Finding all clients");
        return clientRepository.findAll();
    }

    @Override
    public Client updateClient(UUID id, Client client) {
        log.info("Updating client with ID: {}", id);
        
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + id));

        if (client.getName() != null) {
            existingClient.setName(client.getName());
        }
        if (client.getGender() != null) {
            existingClient.setGender(client.getGender());
        }
        if (client.getAge() != null) {
            existingClient.setAge(client.getAge());
        }
        if (client.getIdentification() != null) {
            existingClient.setIdentification(client.getIdentification());
        }
        if (client.getPhone() != null) {
            existingClient.setPhone(client.getPhone());
        }
        if (client.getAddress() != null) {
            existingClient.setAddress(client.getAddress());
        }

        if (client.getPassword() != null) {
            existingClient.setPassword(client.getPassword());
        }
        if (client.getState() != null) {
            existingClient.setState(client.getState());
        }
        
        Client updatedClient = clientRepository.save(existingClient);
        log.info("Client updated successfully with ID: {}", updatedClient.getId());
        return updatedClient;
    }

    @Override
    public void deleteClient(UUID id) {
        log.info("Deleting client with ID: {}", id);
        
        if (!clientRepository.findById(id).isPresent()) {
            throw new ClientNotFoundException("Cliente no encontrado con ID: " + id);
        }
        
        clientRepository.deleteById(id);
        log.info("Client deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByClientId(String clientId) {
        return clientRepository.existsByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentification(String identification) {
        return clientRepository.existsByIdentification(identification);
    }
}
