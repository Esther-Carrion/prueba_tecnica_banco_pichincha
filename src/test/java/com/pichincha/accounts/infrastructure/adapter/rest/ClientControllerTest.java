package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.ClientInputPort;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import com.pichincha.accounts.infrastructure.mapper.ClientDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteDto;
import com.pichincha.infrastructure.adapter.rest.dto.ClienteUpdateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientInputPort clientInputPort;
    @Mock
    private ClientDtoMapper clientDtoMapper;

    @InjectMocks
    private ClientController controller;

    private Client domain(UUID id) {
        Client c = new Client();
        c.setId(id);
        c.setClientId("C1");
        c.setName("Ana");
        return c;
    }

    @Test
    void shouldCreateClientAndReturnCreatedWhenValidRequest() {
        ClienteCreateDto createDto = new ClienteCreateDto();
        Client d = domain(UUID.randomUUID());
        ClienteDto dto = new ClienteDto();
        when(clientDtoMapper.toDomain(createDto)).thenReturn(d);
        when(clientInputPort.createClient(d)).thenReturn(d);
        when(clientDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<ClienteDto> response = controller.createClient(createDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnOkWhenGetClientByIdExists() {
        UUID id = UUID.randomUUID();
        Client d = domain(id);
        ClienteDto dto = new ClienteDto();
        when(clientInputPort.findById(id)).thenReturn(Optional.of(d));
        when(clientDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<ClienteDto> response = controller.getClientById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenGetClientByIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(clientInputPort.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<ClienteDto> response = controller.getClientById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnOkWhenGetClientByClientIdExists() {
        Client d = domain(UUID.randomUUID());
        ClienteDto dto = new ClienteDto();
        when(clientInputPort.findByClientId("C1")).thenReturn(Optional.of(d));
        when(clientDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<ClienteDto> response = controller.getClientByClientId("C1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenGetClientByClientIdDoesNotExist() {
        when(clientInputPort.findByClientId("X")).thenReturn(Optional.empty());
        ResponseEntity<ClienteDto> response = controller.getClientByClientId("X");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnAllClientsWhenGetAllCalled() {
        Client d = domain(UUID.randomUUID());
        ClienteDto dto = new ClienteDto();
        when(clientInputPort.findAll()).thenReturn(List.of(d));
        when(clientDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<List<ClienteDto>> response = controller.getAllClients();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldUpdateAndReturnOkWhenClientExists() {
        UUID id = UUID.randomUUID();
        Client d = domain(id);
        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        ClienteDto dto = new ClienteDto();
        when(clientInputPort.findById(id)).thenReturn(Optional.of(d));
        doAnswer(inv -> null).when(clientDtoMapper).updateDomain(any(Client.class), any(ClienteUpdateDto.class));
        when(clientInputPort.updateClient(id, d)).thenReturn(d);
        when(clientDtoMapper.toDto(d)).thenReturn(dto);

        ResponseEntity<ClienteDto> response = controller.updateClient(id, updateDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenUpdateClientDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(clientInputPort.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<ClienteDto> response = controller.updateClient(id, new ClienteUpdateDto());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteAndReturnNoContentWhenClientExists() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Void> response = controller.deleteClient(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(clientInputPort).deleteClient(id);
    }

    @Test
    void shouldReturnNotFoundWhenDeleteClientThrowsNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new ClientNotFoundException("not found")).when(clientInputPort).deleteClient(id);
        ResponseEntity<Void> response = controller.deleteClient(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnExistsFlagsWhenExistsEndpointsCalled() {
        when(clientInputPort.existsByClientId("C1")).thenReturn(true);
        when(clientInputPort.existsByIdentification("0102")).thenReturn(false);

        assertThat(controller.existsByClientId("C1").getBody()).isTrue();
        assertThat(controller.existsByIdentification("0102").getBody()).isFalse();
    }
}
