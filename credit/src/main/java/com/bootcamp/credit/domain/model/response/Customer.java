package com.bootcamp.credit.domain.model.response;

import com.bootcamp.credit.domain.model.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private String id;
    private String documentNumber;
    private String name;
    private String email;

    private CustomerType type;

    private String lastName;

    private java.util.List<String> authorizedSigners;


    private String profile;

}
