package com.bootcamp.account.infrastructure.repository;

import com.bootcamp.account.domain.model.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountMongoRepository extends MongoRepository<BankAccount, String> {
    List<BankAccount> findByCustomerId(String customerId);
}
