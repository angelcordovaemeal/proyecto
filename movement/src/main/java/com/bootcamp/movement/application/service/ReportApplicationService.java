package com.bootcamp.movement.application.service;

import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.report.BankAccountReport;
import com.bootcamp.movement.domain.model.report.CreditReport;
import com.bootcamp.movement.domain.model.report.GeneralReport;
import com.bootcamp.movement.domain.repository.MovementRepository;
import com.bootcamp.movement.infrastructure.client.AccountClient;
import com.bootcamp.movement.infrastructure.client.CreditClient;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportApplicationService {

    private final AccountClient accountClient;
    private final CreditClient creditClient;
    private final MovementRepository movementRepo;

    public Single<GeneralReport> generateGeneralReport(LocalDateTime start, LocalDateTime end) {

        Single<List<BankAccountReport>> accounts = accountClient.findAll()
                .flatMapPublisher(Flowable::fromIterable)
                .flatMapSingle(acc ->
                        movementRepo.findByProductId(acc.getId())
                                .filter(m -> m.getCreatedAt().isAfter(start)
                                        && m.getCreatedAt().isBefore(end))
                                .toList()
                                .map(movements -> BankAccountReport.builder()
                                        .accountId(acc.getId())
                                        .customerId(acc.getCustomerId())
                                        .type(String.valueOf(acc.getType()))
                                        .balance(acc.getBalance())
                                        .movements(movements)
                                        .build()
                                )
                )
                .toList();

        Single<List<CreditReport>> credits = creditClient.findAll()
                .flatMapPublisher(Flowable::fromIterable)
                .flatMapSingle(cr ->
                        movementRepo.findByProductId(cr.getId())
                                .filter(m -> m.getCreatedAt().isAfter(start)
                                        && m.getCreatedAt().isBefore(end))
                                .toList()
                                .map(movements -> CreditReport.builder()
                                        .creditId(cr.getId())
                                        .customerId(cr.getCustomerId())
                                        .type(cr.getType())
                                        .creditLimit(cr.getCreditLimit())
                                        .usedAmount(cr.getUsedAmount())
                                        .movements(movements)
                                        .build()
                                )
                )
                .toList();

        return Single.zip(accounts, credits,
                (accList, crList) -> GeneralReport.builder()
                        .accounts(accList)
                        .credits(crList)
                        .build()
        );
    }

    public Single<List<Movement>> last10Movements(String productId) {
        return movementRepo.findByProductId(productId)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .take(10)
                .toList();
    }
}
