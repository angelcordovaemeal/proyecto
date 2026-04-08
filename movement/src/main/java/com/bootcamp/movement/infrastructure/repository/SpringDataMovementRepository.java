package com.bootcamp.movement.infrastructure.repository;

import com.bootcamp.movement.domain.model.Movement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataMovementRepository extends MongoRepository<Movement, String> {
    List<Movement> findByProductId(String customerId);
}
