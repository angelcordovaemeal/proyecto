package com.bootcamp.credit.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonalCredit.class, name = "PERSONAL"),
        @JsonSubTypes.Type(value = BusinessCredit.class, name = "BUSINESS"),
        @JsonSubTypes.Type(value = CreditCard.class, name = "CREDIT_CARD")
})
@Document("credits")
public abstract class Credit {

    protected String id;
    protected String customerId;
    protected CreditType type;
    protected Double creditLimit;
    protected Double usedAmount;
    protected Double interestRate;

}
