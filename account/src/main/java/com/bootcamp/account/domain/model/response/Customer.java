package com.bootcamp.account.domain.model.response;

import com.bootcamp.account.domain.model.CustomerType;
import lombok.Data;

@Data
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
