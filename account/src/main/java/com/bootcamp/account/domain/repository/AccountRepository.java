package com.bootcamp.account.domain.repository;

import com.bootcamp.account.domain.model.BankAccount;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface AccountRepository {

    Single<BankAccount> save(BankAccount account);

    Single<List<BankAccount>> findAll();

    Maybe<BankAccount> findById(String id);

    Completable deleteById(String id);

    Single<List<BankAccount>> findByCustomerId(String customerId);
}
