package com.bootcamp.credit.application.service;

import com.bootcamp.credit.application.exception.BusinessException;
import com.bootcamp.credit.application.messages.AppMessages;
import com.bootcamp.credit.domain.model.*;
import com.bootcamp.credit.domain.model.response.Customer;
import com.bootcamp.credit.domain.repository.CreditRepository;
import com.bootcamp.credit.infrastructure.client.CustomerClient;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

    @Mock
    private CreditRepository repository;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private CreditApplicationService service;

    private Customer personalCustomer;
    private Customer businessCustomer;

    @BeforeEach
    void setup() {

        personalCustomer = new Customer();
        personalCustomer.setId("c1");
        personalCustomer.setType(CustomerType.PERSONAL);

        businessCustomer = new Customer();
        businessCustomer.setId("c2");
        businessCustomer.setType(CustomerType.BUSINESS);
    }

    @Test
    void shouldCreatePersonalCreditSuccessfully() {

        PersonalCredit credit = new PersonalCredit();
        credit.setType(CreditType.PERSONAL);
        credit.setCustomerId("c1");

        when(customerClient.getCustomerById("c1"))
                .thenReturn(Single.just(personalCustomer));

        when(repository.findByCustomerId("c1"))
                .thenReturn(Flowable.empty());

        when(repository.save(any(Credit.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        service.createCredit(credit)
                .test()
                .assertComplete()
                .assertValue(c -> c instanceof PersonalCredit);
    }

    @Test
    void shouldFailWhenPersonalCreditForBusinessCustomer() {

        PersonalCredit credit = new PersonalCredit();
        credit.setType(CreditType.PERSONAL);
        credit.setCustomerId("c2");

        when(customerClient.getCustomerById("c2"))
                .thenReturn(Single.just(businessCustomer));

        service.createCredit(credit)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                AppMessages.PERSONAL_CREDIT_ONLY_PERSONAL.equals(e.getMessage())
                );
    }

    @Test
    void shouldFailWhenPersonalCreditAlreadyExists() {

        PersonalCredit credit = new PersonalCredit();
        credit.setType(CreditType.PERSONAL);
        credit.setCustomerId("c1");

        when(customerClient.getCustomerById("c1"))
                .thenReturn(Single.just(personalCustomer));

        when(repository.findByCustomerId("c1"))
                .thenReturn(Flowable.just(credit));

        service.createCredit(credit)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                AppMessages.PERSONAL_CREDIT_EXISTS.equals(e.getMessage())
                );
    }

    @Test
    void shouldCreateBusinessCreditSuccessfully() {

        BusinessCredit credit = new BusinessCredit();
        credit.setType(CreditType.BUSINESS);
        credit.setCustomerId("c2");

        when(customerClient.getCustomerById("c2"))
                .thenReturn(Single.just(businessCustomer));

        when(repository.save(any(Credit.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        service.createCredit(credit)
                .test()
                .assertComplete()
                .assertValue(c -> c instanceof BusinessCredit);
    }

    @Test
    void shouldFailWhenBusinessCreditForPersonalCustomer() {

        BusinessCredit credit = new BusinessCredit();
        credit.setType(CreditType.BUSINESS);
        credit.setCustomerId("c1");

        when(customerClient.getCustomerById("c1"))
                .thenReturn(Single.just(personalCustomer));

        service.createCredit(credit)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                AppMessages.BUSINESS_CREDIT_ONLY_BUSINESS.equals(e.getMessage())
                );
    }

    @Test
    void shouldCreateCreditCardAndCalculateAvailableBalance() {

        CreditCard card = new CreditCard();
        card.setType(CreditType.CREDIT_CARD);
        card.setCustomerId("c1");
        card.setCreditLimit(1000.0);
        card.setUsedAmount(200.0);

        when(customerClient.getCustomerById("c1"))
                .thenReturn(Single.just(personalCustomer));

        when(repository.save(any(Credit.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        service.createCredit(card)
                .test()
                .assertComplete()
                .assertValue(c ->
                        c instanceof CreditCard &&
                                ((CreditCard) c).getAvailableBalance() == 800.0
                );
    }

    @Test
    void shouldUpdateCreditSuccessfully() {

        PersonalCredit existing = new PersonalCredit();
        existing.setId("cr1");
        existing.setType(CreditType.PERSONAL);

        PersonalCredit updated = new PersonalCredit();
        updated.setType(CreditType.PERSONAL);

        when(repository.findById("cr1"))
                .thenReturn(Maybe.just(existing));

        when(repository.save(any(Credit.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        service.updateCredit("cr1", updated)
                .test()
                .assertComplete()
                .assertValue(c -> c.getId().equals("cr1"));
    }

    @Test
    void shouldFailWhenUpdatingDifferentCreditType() {

        PersonalCredit existing = new PersonalCredit();
        existing.setId("cr1");
        existing.setType(CreditType.PERSONAL);

        BusinessCredit updated = new BusinessCredit();
        updated.setType(CreditType.BUSINESS);

        when(repository.findById("cr1"))
                .thenReturn(Maybe.just(existing));

        service.updateCredit("cr1", updated)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                AppMessages.CREDIT_TYPE_CANNOT_CHANGE.equals(e.getMessage())
                );
    }

    @Test
    void shouldRecalculateAvailableBalanceWhenUpdatingCreditCard() {

        CreditCard existing = new CreditCard();
        existing.setId("cr1");
        existing.setType(CreditType.CREDIT_CARD);

        CreditCard updated = new CreditCard();
        updated.setType(CreditType.CREDIT_CARD);
        updated.setCreditLimit(500.0);
        updated.setUsedAmount(100.0);

        when(repository.findById("cr1"))
                .thenReturn(Maybe.just(existing));

        when(repository.save(any(Credit.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        service.updateCredit("cr1", updated)
                .test()
                .assertComplete()
                .assertValue(c ->
                        c instanceof CreditCard &&
                                ((CreditCard) c).getAvailableBalance() == 400.0
                );
    }

    @Test
    void shouldFindCreditById() {

        Credit credit = new PersonalCredit();
        credit.setId("cr1");
        credit.setType(CreditType.PERSONAL);

        when(repository.findById("cr1"))
                .thenReturn(Maybe.just(credit));

        service.findById("cr1")
                .test()
                .assertComplete()
                .assertValue(c -> c.getId().equals("cr1"));
    }

    @Test
    void shouldFindAllCredits() {

        Credit credit = new PersonalCredit();
        credit.setType(CreditType.PERSONAL);

        when(repository.findAll())
                .thenReturn(Flowable.just(credit));

        service.findAll()
                .toList()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFindCreditsByCustomerId() {

        Credit credit = new PersonalCredit();
        credit.setType(CreditType.PERSONAL);
        credit.setCustomerId("c1");

        when(repository.findByCustomerId("c1"))
                .thenReturn(Flowable.just(credit));

        service.findByCustomerId("c1")
                .toList()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldDeleteCreditById() {

        when(repository.deleteById("cr1"))
                .thenReturn(Completable.complete());

        service.deleteById("cr1")
                .test()
                .assertComplete();
    }
}