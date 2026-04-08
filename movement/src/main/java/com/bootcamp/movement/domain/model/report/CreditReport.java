package com.bootcamp.movement.domain.model.report;

import com.bootcamp.movement.domain.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditReport {

    private String creditId;
    private String customerId;
    private String type;
    private Double creditLimit;
    private Double usedAmount;
    private List<Movement> movements;
}
