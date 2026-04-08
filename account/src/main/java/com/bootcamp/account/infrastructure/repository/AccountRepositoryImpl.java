package com.bootcamp.account.infrastructure.repository;

import com.bootcamp.account.domain.model.BankAccount;
import com.bootcamp.account.domain.repository.AccountRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMongoRepository mongo;

    @Override
    public Single<BankAccount> save(BankAccount account) {
        return Single.fromCallable(() -> mongo.save(account));
    }

    @Override
    public Single<List<BankAccount>> findAll() {
        return Single.fromCallable(mongo::findAll);
    }

    @Override
    public Maybe<BankAccount> findById(String id) {
        return Maybe.fromCallable(() -> mongo.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromAction(() -> mongo.deleteById(id));
    }

    @Override
    public Single<List<BankAccount>> findByCustomerId(String customerId) {
        return Single.fromCallable(() -> mongo.findByCustomerId(customerId));
    }
}
