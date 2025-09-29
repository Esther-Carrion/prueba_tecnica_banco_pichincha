package com.pichincha.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessageWhenMessageProvided() {
        String message = "Cuenta no encontrada con ID: 123";
        
        AccountNotFoundException exception = new AccountNotFoundException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageWhenNullProvided() {
        AccountNotFoundException exception = new AccountNotFoundException(null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessageWhenEmptyStringProvided() {
        String message = "";
        
        AccountNotFoundException exception = new AccountNotFoundException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}