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
public class BankAccountReport {

    private String accountId;
    private String customerId;
    private String type;
    private Double balance;

    private List<Movement> movements;
}
