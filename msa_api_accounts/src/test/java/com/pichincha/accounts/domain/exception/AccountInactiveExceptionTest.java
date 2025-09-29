package com.pichincha.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountInactiveExceptionTest {

    @Test
    void shouldCreateExceptionWithMessageWhenMessageProvided() {
        String message = "La cuenta no está activa";
        
        AccountInactiveException exception = new AccountInactiveException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessageWhenNullProvided() {
        AccountInactiveException exception = new AccountInactiveException(null);
        
        assertThat(exception.getMessage()).isNull();
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessageWhenEmptyStringProvided() {
        String message = "";
        
        AccountInactiveException exception = new AccountInactiveException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}