package com.pichincha.accounts.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pichincha.accounts.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("clientId")
    private UUID clientId;
    
    @JsonProperty("client")
    private Client client;
    
    @JsonProperty("accountNumber")
    private String accountNumber;
    
    @JsonProperty("type")
    private AccountType type;
    
    @JsonProperty("initialBalance")
    private BigDecimal initialBalance;
    
    @JsonProperty("currentBalance")
    private BigDecimal currentBalance;
    
    @JsonProperty("state")
    private Boolean state;
}
