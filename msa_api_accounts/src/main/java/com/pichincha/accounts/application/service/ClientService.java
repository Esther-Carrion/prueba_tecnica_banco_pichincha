package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.input.ClientInputPort;
import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import com.pichincha.accounts.util.NumberGenerate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService implements ClientInputPort {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(Client client) {

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
        return savedClient;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findById(UUID id) {
        return clientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findByClientId(String clientId) {

        return clientRepository.findByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {

        return clientRepository.findAll();
    }

    @Override
    public Client updateClient(UUID id, Client client) {

        
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
        return updatedClient;
    }

    @Override
    public void deleteClient(UUID id) {

        
        if (!clientRepository.findById(id).isPresent()) {
            throw new ClientNotFoundException("Cliente no encontrado con ID: " + id);
        }
        
        clientRepository.deleteById(id);
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
