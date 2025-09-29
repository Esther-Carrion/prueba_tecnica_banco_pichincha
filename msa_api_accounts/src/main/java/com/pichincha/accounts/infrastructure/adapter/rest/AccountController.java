package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.AccountInputPort;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.exception.AccountNotFoundException;
import com.pichincha.accounts.infrastructure.mapper.AccountDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaCreateDto;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaDto;
import com.pichincha.infrastructure.adapter.rest.dto.CuentaUpdateDto;
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
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor

@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class AccountController {

    private final AccountInputPort accountInputPort;
    private final AccountDtoMapper accountDtoMapper;

    @PostMapping
    public ResponseEntity<CuentaDto> createAccount(@Valid @RequestBody CuentaCreateDto dto) {
        Account toCreate = accountDtoMapper.toEntity(dto);

        try {
            Account createdAccount = accountInputPort.createAccount(toCreate);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountDtoMapper.toDto(createdAccount));
        } catch (RuntimeException e) {

            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDto> getAccountById(@PathVariable UUID id) {

        Optional<Account> account = accountInputPort.findById(id);
        return account.map(a -> ResponseEntity.ok(accountDtoMapper.toDto(a)))
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<CuentaDto> getAccountByNumber(@PathVariable String accountNumber) {

        Optional<Account> account = accountInputPort.findByAccountNumber(accountNumber);
        return account.map(a -> ResponseEntity.ok(accountDtoMapper.toDto(a)))
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CuentaDto>> getAllAccounts() {

        List<CuentaDto> accounts = accountInputPort.findAll().stream()
                .map(accountDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CuentaDto>> getAccountsByClientId(@PathVariable UUID clientId) {

        List<CuentaDto> accounts = accountInputPort.findByClientId(clientId).stream()
                .map(accountDtoMapper::toDto)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaDto> updateAccount(@PathVariable UUID id, @Valid @RequestBody CuentaUpdateDto dto) {

        try {
            Account existing = accountInputPort.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));
            accountDtoMapper.updateEntity(existing, dto);
            Account updatedAccount = accountInputPort.updateAccount(id, existing);
            return ResponseEntity.ok(accountDtoMapper.toDto(updatedAccount));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {

            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {

        try {
            accountInputPort.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {

            throw e;
        }
    }

    @GetMapping("/exists/{accountNumber}")
    public ResponseEntity<Boolean> existsByAccountNumber(@PathVariable String accountNumber) {
        boolean exists = accountInputPort.existsByAccountNumber(accountNumber);
        return ResponseEntity.ok(exists);
    }
}