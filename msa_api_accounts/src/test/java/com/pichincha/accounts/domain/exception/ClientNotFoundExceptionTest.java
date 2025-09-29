package com.pichincha.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessageWhenMessageProvided() {
        String message = "Cliente no encontrado con ID: 456";
        
        ClientNotFoundException exception = new ClientNotFoundException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCauseWhenBothProvided() {
        String message = "Cliente no encontrado";
        Throwable cause = new IllegalArgumentException("ID inválido");
        
        ClientNotFoundException exception = new ClientNotFoundException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageWhenNullProvided() {
        ClientNotFoundException exception = new ClientNotFoundException(null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageAndCauseWhenBothNullProvided() {
        ClientNotFoundException exception = new ClientNotFoundException(null, null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessageWhenEmptyStringProvided() {
        String message = "";
        
        ClientNotFoundException exception = new ClientNotFoundException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithMessageAndNullCauseWhenCauseIsNull() {
        String message = "Cliente no encontrado";
        
        ClientNotFoundException exception = new ClientNotFoundException(message, null);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}