package com.bootcamp.movement.domain.repository;


import com.bootcamp.movement.domain.model.Movement;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface MovementRepository {

    Single<Movement> save(Movement movement);

    Flowable<Movement> findByProductId(String productId);

    Flowable<Movement> findAll();

    Completable deleteById(String id);
}

