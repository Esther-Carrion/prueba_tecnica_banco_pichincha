package com.pichincha.accounts.infrastructure.config;

import com.pichincha.accounts.domain.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleClientNotFoundExceptionAndReturnNotFound() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleClientNotFoundException(new ClientNotFoundException("cliente X"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("Cliente no encontrado");
        assertThat(response.getBody().getMessage()).contains("cliente X");
    }

    @Test
    void shouldHandleAccountNotFoundExceptionAndReturnNotFound() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAccountNotFoundException(new AccountNotFoundException("cuenta X"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("Cuenta no encontrada");
        assertThat(response.getBody().getMessage()).contains("cuenta X");
    }

    @Test
    void shouldHandleInsufficientFundsExceptionAndReturnBadRequest() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleInsufficientFundsException(new InsufficientFundsException("saldo"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("Saldo insuficiente");
    }

    @Test
    void shouldHandleAccountInactiveExceptionAndReturnBadRequest() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAccountInactiveException(new AccountInactiveException("inactiva"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("Cuenta inactiva");
    }

    @Test
    void shouldHandleInvalidMovementExceptionAndReturnBadRequest() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleInvalidMovementException(new InvalidMovementException("mov"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).contains("Movimiento inválido");
    }

    @Test
    void shouldHandleValidationExceptionsAndReturnBadRequestWithFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));
        MethodArgumentNotValidException ex = org.mockito.Mockito.mock(MethodArgumentNotValidException.class);
        org.mockito.Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getValidationErrors()).containsEntry("name", "must not be blank");
    }

    @Test
    void shouldHandleRuntimeExceptionAndReturnInternalServerError() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleRuntimeException(new RuntimeException("boom"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("boom");
    }

    @Test
    void shouldHandleGenericExceptionAndReturnInternalServerError() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(new Exception("oops"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Ha ocurrido un error inesperado");
    }
}
