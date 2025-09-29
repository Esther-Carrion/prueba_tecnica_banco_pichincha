package com.pichincha.accounts.domain.enums;

public enum MovementType {
    DEPOSITO("Depósito"),
    RETIRO("Retiro"),
    TRANSFERENCIA_IN("Transferencia Entrante"),
    TRANSFERENCIA_OUT("Transferencia Saliente");
    
    private final String description;
    
    MovementType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}