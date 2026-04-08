package com.bootcamp.customer.infrastructure.repository;

import com.bootcamp.customer.domain.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerMongoRepository extends MongoRepository<Customer, String> {
}
