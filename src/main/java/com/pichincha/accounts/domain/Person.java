package com.pichincha.accounts.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.pichincha.accounts.domain.enums.Gender;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("name")
    private String name;

    
    @JsonProperty("gender")
    private Gender gender;
    
    @JsonProperty("age")
    private Integer age;
    
    @JsonProperty("identification")
    private String identification;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("address")
    private String address;
}
