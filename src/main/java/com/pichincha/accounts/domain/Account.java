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
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }
    
    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public Boolean getState() { return state; }
    public void setState(Boolean state) { this.state = state; }
}
