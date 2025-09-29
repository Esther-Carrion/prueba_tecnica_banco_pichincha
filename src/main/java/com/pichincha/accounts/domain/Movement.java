package com.pichincha.accounts.domain;

import com.pichincha.accounts.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    private UUID id;
    private UUID accountId;
    private LocalDateTime date;
    private MovementType movementType;
    private BigDecimal value;
    private BigDecimal balance;
    private Account account;
    
    // Alias methods for compatibility with different naming conventions
    public MovementType getType() { return movementType; }
    public void setType(MovementType type) { this.movementType = type; }
}

