package com.bootcamp.movement.domain.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BankAccount {

    @Id
    private String id;

    private String customerId;
    private AccountType type;
    private Double balance;

    private List<String> holders;
    private List<String> signers;

    private Double maintenanceFee;

    private Double openingMinAmount;
    private Integer freeMovements;
    private Double movementCommission;
    private Double requiredAverageDailyBalance;
    private Double maintenanceCommission;
    private Integer movementCount;

    public BankAccount(String id, String customerId, AccountType type, Double newBalance,
                       Double maintenanceFee, Integer movementCount,
                       Integer freeMovements, Double movementCommission) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.balance = newBalance;
        this.maintenanceFee = maintenanceFee;
        this.movementCount = movementCount;
        this.freeMovements = freeMovements;
        this.movementCommission = movementCommission;
    }

    public BankAccount(String acc1, String savings, String cust1, double v, double v1) {
    }
}