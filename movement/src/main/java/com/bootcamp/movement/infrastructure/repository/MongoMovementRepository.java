package com.bootcamp.movement.infrastructure.repository;

import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.repository.MovementRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class MongoMovementRepository implements MovementRepository {

    private final SpringDataMovementRepository repo;

    @Override
    public Single<Movement> save(Movement m) {
        m.setCreatedAt(LocalDateTime.now());
        return Single.fromCallable(() -> repo.save(m));
    }

    @Override
    public Flowable<Movement> findByProductId(String productId) {
        return Flowable.fromIterable(repo.findByProductId(productId));
    }

    @Override
    public Flowable<Movement> findAll() {
        return Flowable.fromIterable(repo.findAll());
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromAction(() -> repo.deleteById(id));
    }
}
