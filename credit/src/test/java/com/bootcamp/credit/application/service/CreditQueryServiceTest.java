package com.bootcamp.credit.application.service;

import com.bootcamp.credit.domain.model.CreditCard;
import com.bootcamp.credit.domain.model.CreditType;
import com.bootcamp.credit.domain.model.PersonalCredit;
import com.bootcamp.credit.domain.repository.CreditRepository;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditQueryServiceTest {

    @Mock
    private CreditRepository repository;

    @InjectMocks
    private CreditQueryService service;

    @BeforeEach
    void setup() {
    }

    @Test
    void shouldReturnTrueWhenCustomerHasCreditCard() {

        CreditCard card = new CreditCard();
        card.setType(CreditType.CREDIT_CARD);
        card.setCustomerId("c1");

        PersonalCredit personal = new PersonalCredit();
        personal.setType(CreditType.PERSONAL);
        personal.setCustomerId("c1");

        when(repository.findByCustomerId("c1"))
                .thenReturn(Flowable.just(personal, card));

        service.hasCreditCard("c1")
                .test()
                .assertComplete()
                .assertValue(true);
    }

    @Test
    void shouldReturnFalseWhenCustomerHasNoCreditCard() {

        PersonalCredit personal = new PersonalCredit();
        personal.setType(CreditType.PERSONAL);
        personal.setCustomerId("c2");

        when(repository.findByCustomerId("c2"))
                .thenReturn(Flowable.just(personal));

        service.hasCreditCard("c2")
                .test()
                .assertComplete()
                .assertValue(false);
    }

    @Test
    void shouldReturnFalseWhenCustomerHasNoCredits() {

        when(repository.findByCustomerId("c3"))
                .thenReturn(Flowable.empty());

        service.hasCreditCard("c3")
                .test()
                .assertComplete()
                .assertValue(false);
    }
}