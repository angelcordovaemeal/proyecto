package com.bootcamp.account.domain.model;

import com.bootcamp.account.domain.model.AccountType;
import com.bootcamp.account.domain.model.CurrentAccount;
import com.bootcamp.account.domain.model.FixedTermAccount;
import com.bootcamp.account.domain.model.SavingsAccount;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SavingsAccount.class, name = "SAVINGS"),
        @JsonSubTypes.Type(value = CurrentAccount.class, name = "CURRENT"),
        @JsonSubTypes.Type(value = FixedTermAccount.class, name = "FIXED_TERM")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document("accounts")
public abstract class BankAccount {

    @Id
    private String id;

    private String customerId;
    private AccountType type;
    private Double balance;

    private List<String> holders;
    private List<String> signers;

    private Double openingMinAmount;
    private Integer freeMovements;
    private Double movementCommission;

    //para VIP
    private Double requiredAverageDailyBalance;

    //para PYME
    private Double maintenanceCommission;

    private Integer movementCount;


}