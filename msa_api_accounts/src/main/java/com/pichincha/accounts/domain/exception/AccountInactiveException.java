package com.pichincha.accounts.domain.exception;

public class AccountInactiveException extends  RuntimeException {
    public AccountInactiveException(String message) {
        super(message);
    }
}
