package com.bootcamp.credit.domain.repository;

import com.bootcamp.credit.domain.model.Credit;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface CreditRepository {

    Single<Credit> save(Credit credit);

    Flowable<Credit> findAll();

    Maybe<Credit> findById(String id);

    Flowable<Credit> findByCustomerId(String customerId);

    Completable deleteById(String id);

}
