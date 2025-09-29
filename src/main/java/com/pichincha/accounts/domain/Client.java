package com.pichincha.accounts.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends Person {
    
    @JsonProperty("clientId")
    private String clientId;
    
    @JsonProperty("password")
    private String password;
    
    @JsonProperty("state")
    private Boolean state;
    

}
