package com.bootcamp.credit.infrastructure.repository;

import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.domain.repository.CreditRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MongoCreditRepository implements CreditRepository {

    private final SpringDataCreditRepository repo;

    @Override
    public Single<Credit> save(Credit credit) {
        return Single.fromCallable(() -> repo.save(credit));
    }

    @Override
    public Maybe<Credit> findById(String id) {
        return Maybe.fromOptional(repo.findById(id));
    }

    @Override
    public Flowable<Credit> findAll() {
        return Flowable.fromIterable(repo.findAll());
    }

    @Override
    public Flowable<Credit> findByCustomerId(String customerId) {
        return Flowable.fromIterable(repo.findByCustomerId(customerId));
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromAction(() -> repo.deleteById(id));
    }
}