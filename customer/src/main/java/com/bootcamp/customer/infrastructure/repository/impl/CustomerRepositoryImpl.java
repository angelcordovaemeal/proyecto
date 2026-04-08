package com.bootcamp.customer.infrastructure.repository.impl;

import com.bootcamp.customer.domain.model.Customer;
import com.bootcamp.customer.domain.repository.CustomerRepository;
import com.bootcamp.customer.infrastructure.repository.CustomerMongoRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMongoRepository mongo;

    @Override
    public Single<Customer> save(Customer customer) {
        return Single.fromCallable(() -> mongo.save(customer));
    }

    @Override
    public Single<List<Customer>> findAll() {
        return Single.fromCallable(mongo::findAll);
    }

    @Override
    public Maybe<Customer> findById(String id) {
        return Maybe.fromCallable(() -> mongo.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromAction(() -> mongo.deleteById(id));
    }
}