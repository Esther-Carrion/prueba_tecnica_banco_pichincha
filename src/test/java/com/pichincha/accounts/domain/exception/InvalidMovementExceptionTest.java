package com.pichincha.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidMovementExceptionTest {

    @Test
    void shouldCreateExceptionWithMessageWhenMessageProvided() {
        String message = "Movimiento inválido";
        
        InvalidMovementException exception = new InvalidMovementException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCauseWhenBothProvided() {
        String message = "No se permite la modificación de movimientos";
        Throwable cause = new UnsupportedOperationException("Operación no soportada");
        
        InvalidMovementException exception = new InvalidMovementException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageWhenNullProvided() {
        InvalidMovementException exception = new InvalidMovementException(null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageAndCauseWhenBothNullProvided() {
        InvalidMovementException exception = new InvalidMovementException(null, null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessageWhenEmptyStringProvided() {
        String message = "";
        
        InvalidMovementException exception = new InvalidMovementException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithMessageAndNullCauseWhenCauseIsNull() {
        String message = "El valor del movimiento debe ser diferente de cero";
        
        InvalidMovementException exception = new InvalidMovementException(message, null);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}