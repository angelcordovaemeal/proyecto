package com.bootcamp.customer.domain.repository;

import com.bootcamp.customer.domain.model.Customer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface CustomerRepository {

    Single<Customer> save(Customer customer);
    Single<List<Customer>> findAll();
    Maybe<Customer> findById(String id);
    Completable deleteById(String id);
}