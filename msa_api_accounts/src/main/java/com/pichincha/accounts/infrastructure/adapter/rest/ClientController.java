package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.ClientInputPort;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import com.pichincha.accounts.infrastructure.mapper.ClientDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteDto;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class ClientController {

    private final ClientInputPort clientInputPort;
    private final ClientDtoMapper clientDtoMapper;

    @PostMapping
    public ResponseEntity<ClienteDto> createClient(@Valid @RequestBody ClienteCreateDto dto) {

        Client toCreate = clientDtoMapper.toDomain(dto);
        try {
            Client created = clientInputPort.createClient(toCreate);
            return ResponseEntity.status(HttpStatus.CREATED).body(clientDtoMapper.toDto(created));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> getClientById(@PathVariable UUID id) {
        Optional<Client> client = clientInputPort.findById(id);
        return client.map(c -> ResponseEntity.ok(clientDtoMapper.toDto(c)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clientId/{clientId}")
    public ResponseEntity<ClienteDto> getClientByClientId(@PathVariable String clientId) {

        Optional<Client> client = clientInputPort.findByClientId(clientId);
        return client.map(c -> ResponseEntity.ok(clientDtoMapper.toDto(c)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClienteDto>> getAllClients() {

        List<ClienteDto> clients = clientInputPort.findAll().stream()
                .map(clientDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> updateClient(@PathVariable UUID id, @Valid @RequestBody ClienteUpdateDto dto) {

        try {
            Client existing = clientInputPort.findById(id).orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado"));
            clientDtoMapper.updateDomain(existing, dto);
            Client updated = clientInputPort.updateClient(id, existing);
            return ResponseEntity.ok(clientDtoMapper.toDto(updated));
        } catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {

        try {
            clientInputPort.deleteClient(id);
            return ResponseEntity.noContent().build();
        } catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {

            throw e;
        }
    }

    @GetMapping("/exists/clientId/{clientId}")
    public ResponseEntity<Boolean> existsByClientId(@PathVariable String clientId) {
        boolean exists = clientInputPort.existsByClientId(clientId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/identification/{identification}")
    public ResponseEntity<Boolean> existsByIdentification(@PathVariable String identification) {
        boolean exists = clientInputPort.existsByIdentification(identification);
        return ResponseEntity.ok(exists);
    }
}