package com.bootcamp.credit.infrastructure.repository;

import com.bootcamp.credit.domain.model.Credit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataCreditRepository extends MongoRepository<Credit, String> {

    List<Credit> findByCustomerId(String customerId);

}