package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.AccountInputPort;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.exception.AccountNotFoundException;
import com.pichincha.accounts.infrastructure.mapper.AccountDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaDto;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaUpdateDto;
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
class AccountControllerTest {

    @Mock
    private AccountInputPort accountInputPort;
    @Mock
    private AccountDtoMapper accountDtoMapper;

    @InjectMocks
    private AccountController controller;

    private Account account(UUID id) {
        Account a = new Account();
        a.setId(id);
        a.setAccountNumber("N1");
        return a;
    }

    @Test
    void shouldCreateAccountAndReturnCreatedWhenValidRequest() {
        CuentaCreateDto createDto = new CuentaCreateDto();
        Account domain = account(UUID.randomUUID());
        CuentaDto responseDto = new CuentaDto();

        when(accountDtoMapper.toEntity(createDto)).thenReturn(domain);
        when(accountInputPort.createAccount(domain)).thenReturn(domain);
        when(accountDtoMapper.toDto(domain)).thenReturn(responseDto);

        ResponseEntity<CuentaDto> response = controller.createAccount(createDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(responseDto);
    }

    @Test
    void shouldReturnOkWhenGetAccountByIdExists() {
        UUID id = UUID.randomUUID();
        Account domain = account(id);
        CuentaDto dto = new CuentaDto();
        when(accountInputPort.findById(id)).thenReturn(Optional.of(domain));
        when(accountDtoMapper.toDto(domain)).thenReturn(dto);

        ResponseEntity<CuentaDto> response = controller.getAccountById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenGetAccountByIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(accountInputPort.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<CuentaDto> response = controller.getAccountById(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnOkWhenGetAccountByNumberExists() {
        String number = "N1";
        Account domain = account(UUID.randomUUID());
        CuentaDto dto = new CuentaDto();
        when(accountInputPort.findByAccountNumber(number)).thenReturn(Optional.of(domain));
        when(accountDtoMapper.toDto(domain)).thenReturn(dto);

        ResponseEntity<CuentaDto> response = controller.getAccountByNumber(number);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenGetAccountByNumberDoesNotExist() {
        when(accountInputPort.findByAccountNumber("X")).thenReturn(Optional.empty());
        ResponseEntity<CuentaDto> response = controller.getAccountByNumber("X");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnAllAccountsWhenGetAllCalled() {
        Account domain = account(UUID.randomUUID());
        CuentaDto dto = new CuentaDto();
        when(accountInputPort.findAll()).thenReturn(List.of(domain));
        when(accountDtoMapper.toDto(domain)).thenReturn(dto);

        ResponseEntity<List<CuentaDto>> response = controller.getAllAccounts();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnAccountsByClientIdWhenGetByClientIdCalled() {
        UUID clientId = UUID.randomUUID();
        Account domain = account(UUID.randomUUID());
        CuentaDto dto = new CuentaDto();
        when(accountInputPort.findByClientId(clientId)).thenReturn(List.of(domain));
        when(accountDtoMapper.toDto(domain)).thenReturn(dto);

        ResponseEntity<List<CuentaDto>> response = controller.getAccountsByClientId(clientId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldUpdateAndReturnOkWhenAccountExists() {
        UUID id = UUID.randomUUID();
        Account domain = account(id);
        CuentaUpdateDto updateDto = new CuentaUpdateDto();
        CuentaDto dto = new CuentaDto();
        when(accountInputPort.findById(id)).thenReturn(Optional.of(domain));
        // updateEntity mutates domain; we can no-op
        doAnswer(inv -> null).when(accountDtoMapper).updateEntity(any(Account.class), any(CuentaUpdateDto.class));
        when(accountInputPort.updateAccount(id, domain)).thenReturn(domain);
        when(accountDtoMapper.toDto(domain)).thenReturn(dto);

        ResponseEntity<CuentaDto> response = controller.updateAccount(id, updateDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenUpdateAccountDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(accountInputPort.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<CuentaDto> response = controller.updateAccount(id, new CuentaUpdateDto());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteAndReturnNoContentWhenAccountExists() {
        UUID id = UUID.randomUUID();
        ResponseEntity<Void> response = controller.deleteAccount(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(accountInputPort).deleteAccount(id);
    }

    @Test
    void shouldReturnNotFoundWhenDeleteAccountThrowsNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new AccountNotFoundException("not found")).when(accountInputPort).deleteAccount(id);
        ResponseEntity<Void> response = controller.deleteAccount(id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnExistsFlagWhenExistsByAccountNumberCalled() {
        when(accountInputPort.existsByAccountNumber("N1")).thenReturn(true);
        ResponseEntity<Boolean> response = controller.existsByAccountNumber("N1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }
}
