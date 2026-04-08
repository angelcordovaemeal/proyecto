package com.bootcamp.movement.application.service;

import com.bootcamp.movement.application.exception.BusinessException;
import com.bootcamp.movement.application.messages.AppMessages;
import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.MovementType;
import com.bootcamp.movement.domain.model.TransferRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementApplicationServiceTest {

    @Mock
    private MovementRepository repository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private CreditClient creditClient;

    @InjectMocks
    private MovementApplicationService service;

    private BankAccount account;
    private Credit credit;

    @BeforeEach
    void setup() {

        account = new BankAccount(
                "acc1",
                "cust1",
                AccountType.SAVINGS,
                1000.0,
                0.0,
                0,
                1,
                2.0
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
    }


    @Test
    void shouldRegisterDepositSuccessfully() {

        Movement m = new Movement();
        m.setProductId("acc1");
        m.setAmount(100.0);

        when(accountClient.getAccount("acc1"))
                .thenReturn(Single.just(account));

        when(repository.save(any(Movement.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        when(accountClient.updateAccount(any(BankAccount.class)))
                .thenReturn(Single.just(account));

        service.registerDeposit(m)
                .test()
                .assertComplete()
                .assertValue(res -> res.getType() == MovementType.DEPOSIT);
    }

    @Test
    void shouldFailDepositWithInvalidAmount() {

        Movement m = new Movement();
        m.setProductId("acc1");
        m.setAmount(-10.0);

        when(accountClient.getAccount("acc1"))
                .thenReturn(Single.just(account));

        service.registerDeposit(m)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals(AppMessages.INVALID_DEPOSIT_AMOUNT)
                );
    }


    @Test
    void shouldRegisterWithdrawalSuccessfully() {

        Movement m = new Movement();
        m.setProductId("acc1");
        m.setAmount(100.0);

        when(accountClient.getAccount("acc1"))
                .thenReturn(Single.just(account));

        when(repository.save(any(Movement.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        when(accountClient.updateAccount(any(BankAccount.class)))
                .thenReturn(Single.just(account));

        service.registerWithdrawal(m)
                .test()
                .assertComplete()
                .assertValue(res -> res.getType() == MovementType.WITHDRAWAL);
    }

    @Test
    void shouldFailWithdrawalWhenInsufficientBalance() {

        Movement m = new Movement();
        m.setProductId("acc1");
        m.setAmount(5000.0);

        when(accountClient.getAccount("acc1"))
                .thenReturn(Single.just(account));

        service.registerWithdrawal(m)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals(AppMessages.INSUFFICIENT_BALANCE)
                );
    }


    @Test
    void shouldRegisterCreditPaymentSuccessfully() {

        Movement m = new Movement();
        m.setProductId("cr1");
        m.setAmount(100.0);

        when(creditClient.getCredit("cr1"))
                .thenReturn(Single.just(credit));

        when(repository.save(any(Movement.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        when(creditClient.updateCredit(any(Credit.class)))
                .thenReturn(Single.just(credit));

        service.registerCreditPayment(m)
                .test()
                .assertComplete()
                .assertValue(res -> res.getType() == MovementType.CREDIT_PAYMENT);
    }


    @Test
    void shouldRegisterCreditConsumptionSuccessfully() {

        Movement m = new Movement();
        m.setProductId("cr1");
        m.setAmount(100.0);

        when(creditClient.getCredit("cr1"))
                .thenReturn(Single.just(credit));

        when(repository.save(any(Movement.class)))
                .thenAnswer(inv -> Single.just(inv.getArgument(0)));

        when(creditClient.updateCredit(any(Credit.class)))
                .thenReturn(Single.just(credit));

        service.registerCreditConsumption(m)
                .test()
                .assertComplete()
                .assertValue(res -> res.getType() == MovementType.CREDIT_CONSUMPTION);
    }


    @Test
    void shouldTransferSuccessfully() {

        TransferRequest req = new TransferRequest("acc1", "acc2", 100.0);

        BankAccount toAcc = new BankAccount(
                "acc2",
                "cust2",
                AccountType.SAVINGS,
                500.0,
                0.0,
                0,
                1,
                0.0
        );

        when(accountClient.getAccount("acc1"))
                .thenReturn(Single.just(account));
        when(accountClient.getAccount("acc2"))
                .thenReturn(Single.just(toAcc));

        when(repository.save(any(Movement.class)))
                .thenAnswer(inv -> {
                    Movement mov = inv.getArgument(0);
                    mov.setId("m1");
                    mov.setCreatedAt(LocalDateTime.now());
                    return Single.just(mov);
                });

        when(accountClient.updateAccount(any(BankAccount.class)))
                .thenReturn(Single.just(account));

        service.transfer(req)
                .test()
                .assertComplete()
                .assertValue(m -> m.getType() == MovementType.TRANSFER_OUT);
    }

    @Test
    void shouldFailTransferWithInvalidAmount() {

        TransferRequest req = new TransferRequest("acc1", "acc2", -10.0);

        service.transfer(req)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals(AppMessages.TRANSFER_AMOUNT_INVALID)
                );
    }


    @Test
    void shouldFindMovementsByProduct() {

        Movement m = new Movement();
        m.setProductId("acc1");

        when(repository.findByProductId("acc1"))
                .thenReturn(Flowable.just(m));

        service.findByProduct("acc1")
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFindAllMovements() {

        Movement m = new Movement();

        when(repository.findAll())
                .thenReturn(Flowable.just(m));

        service.findAll()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }
}