package com.bootcamp.credit.application.service;

import com.bootcamp.credit.domain.model.CreditType;
import com.bootcamp.credit.domain.repository.CreditRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditQueryService {

    private final CreditRepository repo;

    public Single<Boolean> hasCreditCard(String customerId) {
        return repo.findByCustomerId(customerId)
                .toList()
                .map(list -> list.stream()
                        .anyMatch(c -> c.getType().equals(CreditType.CREDIT_CARD)));
    }
}
