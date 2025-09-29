package com.pichincha.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsufficientFundsExceptionTest {

    @Test
    void shouldCreateExceptionWithMessageWhenMessageProvided() {
        String message = "Saldo no disponible";
        
        InsufficientFundsException exception = new InsufficientFundsException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCauseWhenBothProvided() {
        String message = "Fondos insuficientes para la operación";
        Throwable cause = new ArithmeticException("Balance negativo");
        
        InsufficientFundsException exception = new InsufficientFundsException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageWhenNullProvided() {
        InsufficientFundsException exception = new InsufficientFundsException(null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageAndCauseWhenBothNullProvided() {
        InsufficientFundsException exception = new InsufficientFundsException(null, null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessageWhenEmptyStringProvided() {
        String message = "";
        
        InsufficientFundsException exception = new InsufficientFundsException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithMessageAndNullCauseWhenCauseIsNull() {
        String message = "Saldo insuficiente";
        
        InsufficientFundsException exception = new InsufficientFundsException(message, null);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}