package com.bootcamp.customer.application.service;

import com.bootcamp.customer.application.messages.AppMessages;
import com.bootcamp.customer.domain.model.Customer;
import com.bootcamp.customer.domain.repository.CustomerRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerApplicationService {

    private final CustomerRepository repository;

    public Single<Customer> create(Customer customer) {
        return repository.save(customer);
    }

    public Single<List<Customer>> findAll() {
        return repository.findAll();
    }

    public Maybe<Customer> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(new RuntimeException(AppMessages.CUSTOMER_NOT_FOUND)));
    }

    public Single<Customer> update(String id, Customer customer) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(new RuntimeException(AppMessages.CUSTOMER_NOT_FOUND)))
                .toSingle()
                .flatMap(existing -> {
                    customer.setId(id);
                    return repository.save(customer);
                });
    }

    public Completable delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(new RuntimeException(AppMessages.CUSTOMER_NOT_FOUND)))
                .flatMapCompletable(c -> repository.deleteById(id));
    }
}
