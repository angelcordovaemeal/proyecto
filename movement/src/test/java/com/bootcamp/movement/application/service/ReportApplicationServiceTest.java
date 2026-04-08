package com.bootcamp.movement.application.service;

import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.report.GeneralReport;
import com.bootcamp.movement.domain.model.response.AccountType;
import com.bootcamp.movement.domain.model.response.BankAccount;
import com.bootcamp.movement.domain.model.response.Credit;
import com.bootcamp.movement.domain.repository.MovementRepository;
import com.bootcamp.movement.infrastructure.client.AccountClient;
import com.bootcamp.movement.infrastructure.client.CreditClient;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportApplicationServiceTest {

    @Mock
    private AccountClient accountClient;

    @Mock
    private CreditClient creditClient;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private ReportApplicationService service;

    private BankAccount account;
    private Credit credit;
    private Movement movement;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setup() {

        start = LocalDateTime.now().minusDays(10);
        end = LocalDateTime.now().plusDays(1);

        account = new BankAccount(
                "acc1",
                "cust1",
                AccountType.SAVINGS,
                1000.0,
                0.0,
                0,
                5,
                1.0
        );

        credit = new Credit(
                "cr1",
                "CREDIT_CARD",
                "cust1",
                1000.0,
                200.0,
                800.0,
                0.1
        );

        movement = new Movement();
        movement.setProductId("acc1");
        movement.setAmount(100.0);
        movement.setCreatedAt(LocalDateTime.now());
    }


    @Test
    void shouldGenerateGeneralReport() {

        when(accountClient.findAll())
                .thenReturn(Single.just(List.of(account)));

        when(creditClient.findAll())
                .thenReturn(Single.just(List.of(credit)));

        when(movementRepository.findByProductId("acc1"))
                .thenReturn(Flowable.just(movement));

        when(movementRepository.findByProductId("cr1"))
                .thenReturn(Flowable.empty());

        service.generateGeneralReport(start, end)
                .test()
                .assertComplete()
                .assertValue(report -> {
                    assert report.getAccounts().size() == 1;
                    assert report.getCredits().size() == 1;
                    return true;
                });
    }

    @Test
    void shouldReturnLast10MovementsOrderedByDate() {

        List<Movement> movements = List.of(
                movementWithDate(1),
                movementWithDate(2),
                movementWithDate(3),
                movementWithDate(4),
                movementWithDate(5),
                movementWithDate(6),
                movementWithDate(7),
                movementWithDate(8),
                movementWithDate(9),
                movementWithDate(10),
                movementWithDate(11)
        );

        when(movementRepository.findByProductId("acc1"))
                .thenReturn(Flowable.fromIterable(movements));

        service.last10Movements("acc1")
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 10)
                .assertValue(list ->
                        list.get(0).getCreatedAt()
                                .isAfter(list.get(9).getCreatedAt())
                );
    }


    private Movement movementWithDate(int daysAgo) {
        Movement m = new Movement();
        m.setProductId("acc1");
        m.setAmount(50.0);
        m.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));
        return m;
    }
}