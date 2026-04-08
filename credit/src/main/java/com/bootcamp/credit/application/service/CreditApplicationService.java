package com.bootcamp.credit.application.service;

import com.bootcamp.credit.application.exception.BusinessException;
import com.bootcamp.credit.application.messages.AppMessages;
import com.bootcamp.credit.domain.model.BusinessCredit;
import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.domain.model.CreditCard;
import com.bootcamp.credit.domain.model.CustomerType;
import com.bootcamp.credit.domain.model.PersonalCredit;
import com.bootcamp.credit.domain.model.response.Customer;
import com.bootcamp.credit.domain.repository.CreditRepository;
import com.bootcamp.credit.infrastructure.client.CustomerClient;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditApplicationService {

    private final CreditRepository repository;
    private final CustomerClient customerClient;

    public Single<Credit> createCredit(Credit credit) {
        return customerClient.getCustomerById(credit.getCustomerId())
                .flatMap(customer -> validateCreationRules(credit, customer))
                .flatMap(repository::save);
    }

    public Maybe<Credit> findById(String id) {
        return repository.findById(id);
    }

    public Flowable<Credit> findAll() {
        return repository.findAll();
    }

    public Flowable<Credit> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Single<Credit> updateCredit(String id, Credit updated) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(new BusinessException(AppMessages.CREDIT_NOT_FOUND)))
                .toSingle()
                .flatMap(existing -> validateUpdate(existing, updated))
                .flatMap(repository::save);
    }

    public Completable deleteById(String id) {
        return repository.deleteById(id);
    }

    private Single<Credit> validateCreationRules(Credit credit, Customer customer) {

        return switch (credit.getType()) {
            case PERSONAL -> validatePersonalCreditCreation((PersonalCredit) credit, customer);
            case BUSINESS -> validateBusinessCreditCreation((BusinessCredit) credit, customer);
            case CREDIT_CARD -> validateCreditCardCreation((CreditCard) credit, customer);
            default -> Single.error(new BusinessException(AppMessages.UNSUPPORTED_CREDIT_TYPE));
        };
    }

    private Single<Credit> validatePersonalCreditCreation(PersonalCredit credit, Customer customer) {

        if (!CustomerType.PERSONAL.equals(customer.getType())) {
            return Single.error(new BusinessException(AppMessages.PERSONAL_CREDIT_ONLY_PERSONAL));
        }
        return repository.findByCustomerId(customer.getId())
                .filter(c -> c instanceof PersonalCredit)
                .firstElement()
                .flatMap(existing ->
                        Maybe.<Credit>error(new BusinessException(AppMessages.PERSONAL_CREDIT_EXISTS))
                )
                .switchIfEmpty(Single.just(credit));
    }

    private Single<Credit> validateBusinessCreditCreation(BusinessCredit credit, Customer customer) {

        if (!customer.getType().equals(CustomerType.BUSINESS)) {
            return Single.error(new BusinessException(AppMessages.BUSINESS_CREDIT_ONLY_BUSINESS));
        }
        return Single.just(credit);
    }

    private Single<Credit> validateCreditCardCreation(CreditCard card, Customer customer) {

        card.setAvailableBalance(card.getCreditLimit() - card.getUsedAmount());

        return Single.just(card);
    }

    private Single<Credit> validateUpdate(Credit existing, Credit updated) {

        updated.setId(existing.getId());

        if (!existing.getType().equals(updated.getType())) {
            return Single.error(new BusinessException(AppMessages.CREDIT_TYPE_CANNOT_CHANGE));
        }
        if (updated instanceof CreditCard card) {
            card.setAvailableBalance(card.getCreditLimit() - card.getUsedAmount());
        }
        return Single.just(updated);
    }
}