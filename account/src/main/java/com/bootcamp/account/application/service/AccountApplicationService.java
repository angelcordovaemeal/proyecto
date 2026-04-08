package com.bootcamp.account.application.service;

import com.bootcamp.account.application.client.CreditClient;
import com.bootcamp.account.application.client.CustomerClient;
import com.bootcamp.account.application.messages.AppMessages;
import com.bootcamp.account.domain.model.AccountType;
import com.bootcamp.account.domain.model.BankAccount;
import com.bootcamp.account.domain.model.CustomerType;
import com.bootcamp.account.domain.model.response.Customer;
import com.bootcamp.account.domain.repository.AccountRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountApplicationService {

    private final AccountRepository repository;
    private final CustomerClient customerClient;
    private final CreditClient creditClient;

    @Value("${account.vip.minimumAverageDailyBalance}")
    private Double vipMinAvg;

    public Single<BankAccount> create(BankAccount account) {
        return customerClient.getCustomer(account.getCustomerId())
                .flatMap(customer -> validateCustomerRules(customer, account))
                .flatMap(repository::save);
    }

    public Single<List<BankAccount>> findAll() {
        return repository.findAll();
    }

    public Maybe<BankAccount> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(new RuntimeException(AppMessages.ACCOUNT_NOT_FOUND)));
    }

    public Single<BankAccount> update(String id, BankAccount account) {
        return findById(id)
                .toSingle()
                .flatMap(existing -> {
                    account.setId(id);
                    return repository.save(account);
                });
    }

    public Completable delete(String id) {
        return findById(id)
                .flatMapCompletable(acc -> repository.deleteById(id));
    }

    public Single<BankAccount> validateCustomerRules(Customer customer, BankAccount account) {
        return repository.findByCustomerId(customer.getId())
                .flatMap(existingAccounts -> {

                    if (customer.getType() == CustomerType.PERSONAL) {
                        return validatePersonal(existingAccounts, account)
                                .flatMap(acc -> validateProfileRules(customer, acc));
                    }
                    if (customer.getType() == CustomerType.BUSINESS) {
                        return validateBusiness(existingAccounts, account)
                                .flatMap(acc -> validateProfileRules(customer, acc));
                    }
                    return Single.error(new RuntimeException(AppMessages.UNKNOWN_CUSTOMER_TYPE));
                });
    }

    private Single<BankAccount> validateProfileRules(Customer customer, BankAccount account) {
        String profile = customer.getProfile();

        if ("VIP".equalsIgnoreCase(profile)) {return validateVIP(customer, account);}
        if ("PYME".equalsIgnoreCase(profile)) {return validatePYME(customer, account);}

        return Single.just(account);
    }

    private Single<BankAccount> validatePersonal(List<BankAccount> existing, BankAccount account) {
        long savings = existing.stream().filter(a -> a.getType() == AccountType.SAVINGS).count();
        long current = existing.stream().filter(a -> a.getType() == AccountType.CURRENT).count();

        if (account.getType() == AccountType.SAVINGS && savings >= 1) {
            return Single.error(new RuntimeException(AppMessages.PERSONAL_SAVINGS_LIMIT));
        }
        if (account.getType() == AccountType.CURRENT && current >= 1) {
            return Single.error(new RuntimeException(AppMessages.PERSONAL_CURRENT_LIMIT));
        }
        return Single.just(account);
    }

    private Single<BankAccount> validateBusiness(List<BankAccount> existing, BankAccount account) {
        if (account.getType() == AccountType.SAVINGS) {
            return Single.error(new RuntimeException(AppMessages.BUSINESS_SAVINGS_FORBIDDEN));
        }
        if (account.getType() == AccountType.FIXED_TERM) {
            return Single.error(new RuntimeException(AppMessages.BUSINESS_FIXED_TERM_FORBIDDEN));
        }
        return Single.just(account);
    }

    private Single<BankAccount> validateVIP(Customer customer, BankAccount account) {

        if (customer.getType() != CustomerType.PERSONAL) {
            return Single.error(new RuntimeException(AppMessages.VIP_ONLY_PERSONAL));
        }
        if (account.getType() != AccountType.SAVINGS) {
            return Single.error(new RuntimeException(AppMessages.VIP_SAVINGS_REQUIRED));
        }
        return creditClient.hasCreditCard(customer.getId())
                .flatMap(hasCard -> {

                    if (!hasCard) {
                        return Single.error(new RuntimeException(AppMessages.VIP_NEEDS_CREDIT_CARD));
                    }
                    if (account.getFreeMovements() == null) {account.setFreeMovements(10);}
                    if (account.getMovementCommission() == null) {account.setMovementCommission(5.0);}
                    if (account.getRequiredAverageDailyBalance() == null) {account.setRequiredAverageDailyBalance(vipMinAvg);}
                    if (account.getMaintenanceCommission() == null) {account.setMaintenanceCommission(0.0);}
                    if (account.getMovementCount() == null) {account.setMovementCount(0);}

                    return Single.just(account);
                });
    }

    private Single<BankAccount> validatePYME(Customer customer, BankAccount account) {

        if (customer.getType() != CustomerType.BUSINESS) {
            return Single.error(new RuntimeException(AppMessages.PYME_ONLY_BUSINESS));
        }
        if (account.getType() != AccountType.CURRENT) {
            return Single.error(new RuntimeException(AppMessages.PYME_CURRENT_REQUIRED));
        }
        return creditClient.hasCreditCard(customer.getId())
                .flatMap(hasCard -> {

                    if (!hasCard) {
                        return Single.error(new RuntimeException(AppMessages.PYME_NEEDS_CREDIT_CARD));
                    }
                    if (account.getFreeMovements() == null) {account.setFreeMovements(20);}
                    if (account.getMovementCommission() == null) {account.setMovementCommission(3.0);}
                    if (account.getRequiredAverageDailyBalance() == null) {account.setRequiredAverageDailyBalance(0.0);}
                    if (account.getMaintenanceCommission() == null) {account.setMaintenanceCommission(0.0);}
                    if (account.getMovementCount() == null) {account.setMovementCount(0);}

                    return Single.just(account);
                });
    }

    public Single<BankAccount> applyMovement(String accountId, Double amount, Double commission) {
        return repository.findById(accountId)
                .switchIfEmpty(Maybe.error(new RuntimeException(AppMessages.ACCOUNT_NOT_FOUND)))
                .toSingle()
                .flatMap(acc -> {

                    double finalAmount = amount - commission;

                    if (acc.getBalance() + finalAmount < 0) {
                        return Single.error(new RuntimeException(AppMessages.INSUFFICIENT_FUNDS));
                    }

                    acc.setBalance(acc.getBalance() + finalAmount);
                    acc.setMovementCount(acc.getMovementCount() + 1);

                    return repository.save(acc);
                });
    }
}