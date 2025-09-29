package com.pichincha.accounts.infrastructure.adapter.persistence.entity;

import com.pichincha.accounts.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Campos heredados de Person
    @Column(name = "nombre", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Gender gender;

    @Column(name = "edad")
    private Integer age;

    @Column(name = "identificacion", unique = true, nullable = false)
    private String identification;

    @Column(name = "telefono")
    private String phone;

    @Column(name = "direccion")
    private String address;

    // Campos específicos de Client
    @Column(name = "cliente_id", unique = true, nullable = false)
    private String clientId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "estado", nullable = false)
    private Boolean state = true;
}
