package com.pichincha.accounts.infrastructure.adapter.persistence.entity;

import com.pichincha.accounts.domain.enums.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "cuenta_id", nullable = false)
    private UUID accountId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private MovementType movementType;

    @Column(name = "valor", precision = 15, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "saldo_despues", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", insertable = false, updatable = false)
    private AccountEntity account;
}
