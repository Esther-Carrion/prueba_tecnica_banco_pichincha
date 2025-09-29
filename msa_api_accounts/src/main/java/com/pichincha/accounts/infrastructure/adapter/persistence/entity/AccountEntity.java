package com.pichincha.accounts.infrastructure.adapter.persistence.entity;

import com.pichincha.accounts.domain.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cuenta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clientId;

    @Column(name = "numero_cuenta", unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private AccountType type;

    @Column(name = "saldo_inicial", precision = 15, scale = 2, nullable = false)
    private BigDecimal initialBalance;

    @Column(name = "saldo_actual", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "estado", nullable = false)
    private Boolean state = true;
}
