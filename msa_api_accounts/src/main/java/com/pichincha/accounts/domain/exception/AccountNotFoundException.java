package com.pichincha.accounts.domain.exception;

public class AccountNotFoundException extends  RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
