package com.bootcamp.movement.domain.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit {

    private String id;
    private String type;
    private String customerId;
    private Double creditLimit;
    private Double usedAmount;
    private Double availableBalance;
    private Double interestRate;

    public Double getAvailableBalance(){
        return creditLimit-usedAmount;
    }

}
