package com.bootcamp.movement.application.service;

import com.bootcamp.movement.application.exception.BusinessException;
import com.bootcamp.movement.application.messages.AppMessages;
import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.MovementType;
import com.bootcamp.movement.domain.model.TransferRequest;
import com.bootcamp.movement.domain.model.response.BankAccount;
import com.bootcamp.movement.domain.model.response.Credit;
import com.bootcamp.movement.domain.repository.MovementRepository;
import com.bootcamp.movement.infrastructure.client.AccountClient;
import com.bootcamp.movement.infrastructure.client.CreditClient;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovementApplicationService {

    private final MovementRepository repository;
    private final AccountClient accountClient;
    private final CreditClient creditClient;

    public Single<Movement> registerDeposit(Movement m) {

        return accountClient.getAccount(m.getProductId())
                .flatMap(account -> {

                    if (m.getAmount() <= 0) {
                        return Single.error(new BusinessException(AppMessages.INVALID_DEPOSIT_AMOUNT));
                    }
                    return calculateCommission(account)
                            .flatMap(commission -> {

                                double finalAmount = m.getAmount() - commission;

                                if (finalAmount <= 0) {
                                    return Single.error(new BusinessException(AppMessages.AMOUNT_TOO_LOW_AFTER_COMMISSION));
                                }
                                double newBalance = account.getBalance() + finalAmount;

                                BankAccount updated = new BankAccount(
                                        account.getId(),
                                        account.getCustomerId(),
                                        account.getType(),
                                        newBalance,
                                        account.getMaintenanceFee(),
                                        account.getMovementCount() + 1,
                                        account.getFreeMovements(),
                                        account.getMovementCommission()
                                );

                                m.setType(MovementType.DEPOSIT);
                                m.setCreatedAt(LocalDateTime.now());
                                m.setCommission(commission);

                                return repository.save(m)
                                        .flatMap(saved ->
                                                accountClient.updateAccount(updated)
                                                        .map(acc -> saved)
                                        );
                            });
                });
    }

    public Single<Movement> registerWithdrawal(Movement m) {

        return accountClient.getAccount(m.getProductId())
                .flatMap(account -> {

                    if (m.getAmount() <= 0) {
                        return Single.error(new BusinessException(AppMessages.INVALID_WITHDRAWAL_AMOUNT));
                    }
                    return calculateCommission(account)
                            .flatMap(commission -> {

                                double totalDebit = m.getAmount() + commission;

                                if (account.getBalance() < totalDebit) {
                                    return Single.error(new BusinessException(AppMessages.INSUFFICIENT_BALANCE));
                                }
                                double newBalance = account.getBalance() - totalDebit;

                                BankAccount updated = new BankAccount(
                                        account.getId(),
                                        account.getCustomerId(),
                                        account.getType(),
                                        newBalance,
                                        account.getMaintenanceFee(),
                                        account.getMovementCount() + 1,
                                        account.getFreeMovements(),
                                        account.getMovementCommission()
                                );

                                m.setType(MovementType.WITHDRAWAL);
                                m.setCommission(commission);
                                m.setCreatedAt(LocalDateTime.now());

                                return repository.save(m)
                                        .flatMap(saved ->
                                                accountClient.updateAccount(updated)
                                                        .map(acc -> saved)
                                        );
                            });
                });
    }

    public Single<Movement> registerCreditPayment(Movement m) {

        return creditClient.getCredit(m.getProductId())
                .flatMap(credit -> {

                    if (m.getAmount() == null || m.getAmount() <= 0) {
                        return Single.error(new BusinessException(AppMessages.INVALID_PAYMENT_AMOUNT));
                    }
                    if (credit.getUsedAmount() < m.getAmount()) {
                        return Single.error(new BusinessException(AppMessages.INSUFFICIENT_CREDIT_LIMIT));
                    }
                    Double newUsedAmount = credit.getUsedAmount() - m.getAmount();
                    Double newAvailable = credit.getCreditLimit() - newUsedAmount;

                    Credit updated = new Credit(
                            credit.getId(),
                            credit.getType(),
                            credit.getCustomerId(),
                            credit.getCreditLimit(),
                            newUsedAmount,
                            newAvailable,
                            credit.getInterestRate()
                    );

                    m.setType(MovementType.CREDIT_PAYMENT);
                    m.setCreatedAt(LocalDateTime.now());

                    return repository.save(m)
                            .flatMap(savedMovement ->
                                    creditClient.updateCredit(updated)
                                            .map(res -> savedMovement)
                            );
                });
    }

    public Single<Movement> registerCreditConsumption(Movement m) {

        return creditClient.getCredit(m.getProductId())
                .flatMap(credit -> {

                    if (m.getAmount() == null || m.getAmount() <= 0) {
                        return Single.error(new BusinessException(AppMessages.INVALID_CONSUMPTION_AMOUNT));
                    }
                    if (!"CREDIT_CARD".equalsIgnoreCase(credit.getType())) {
                        return Single.error(new BusinessException(AppMessages.ONLY_CREDIT_CARD_ALLOWED));
                    }
                    if (credit.getAvailableBalance() < m.getAmount()) {
                        return Single.error(new BusinessException(AppMessages.INSUFFICIENT_CREDIT_LIMIT));
                    }
                    Double newUsedAmount = credit.getUsedAmount() + m.getAmount();
                    Double newAvailableBalance = credit.getCreditLimit() - newUsedAmount;

                    m.setType(MovementType.CREDIT_CONSUMPTION);
                    m.setCreatedAt(LocalDateTime.now());

                    Credit updated = new Credit(
                            credit.getId(),
                            credit.getType(),
                            credit.getCustomerId(),
                            credit.getCreditLimit(),
                            newUsedAmount,
                            newAvailableBalance,
                            credit.getInterestRate()
                    );

                    return repository.save(m)
                            .flatMap(savedMovement ->
                                    creditClient.updateCredit(updated)
                                            .map(res -> savedMovement)
                            );
                });
    }

    public Single<List<Movement>> findByProduct(String productId) {
        return repository.findByProductId(productId).toList();
    }

    public Single<List<Movement>> findAll() {
        return repository.findAll().toList();
    }

    private Single<Double> calculateCommission(BankAccount acc) {
        int count = acc.getMovementCount();
        int free = acc.getFreeMovements();

        if (count < free) {
            return Single.just(0.0);
        }
        return Single.just(acc.getMovementCommission());
    }

    public Single<Movement> transfer(TransferRequest req) {

        String fromId = req.getFromAccountId();
        String toId = req.getToAccountId();
        Double amount = req.getAmount();

        if (amount == null || amount <= 0) {
            return Single.error(new BusinessException(AppMessages.TRANSFER_AMOUNT_INVALID));
        }
        return accountClient.getAccount(fromId)
                .flatMap(fromAcc ->
                        accountClient.getAccount(toId)
                                .flatMap(toAcc ->
                                        processTransfer(fromAcc, toAcc, amount)
                                )
                );
    }

    private Single<Movement> processTransfer(BankAccount fromAcc, BankAccount toAcc, Double amount) {

        if (fromAcc.getBalance() < amount) {
            return Single.error(new BusinessException(AppMessages.INSUFFICIENT_BALANCE));
        }
        return calculateCommission(fromAcc)
                .flatMap(commissionOut -> {

                    double totalDebit = amount + commissionOut;

                    if (fromAcc.getBalance() < totalDebit) {
                        return Single.error(new BusinessException(AppMessages.INSUFFICIENT_BALANCE_WITH_COMMISSION));
                    }
                    BankAccount updatedFrom = new BankAccount(
                            fromAcc.getId(),
                            fromAcc.getCustomerId(),
                            fromAcc.getType(),
                            fromAcc.getBalance() - totalDebit,
                            fromAcc.getMaintenanceFee(),
                            fromAcc.getMovementCount() + 1,
                            fromAcc.getFreeMovements(),
                            fromAcc.getMovementCommission()
                    );

                    Movement movOut = Movement.builder()
                            .productId(fromAcc.getId())
                            .type(MovementType.TRANSFER_OUT)
                            .amount(amount)
                            .commission(commissionOut)
                            .description("Transfer to " + toAcc.getId())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return repository.save(movOut)
                            .flatMap(savedOut ->
                                    applyTransferIn(toAcc, amount)
                                            .flatMap(savedIn ->
                                                    accountClient.updateAccount(updatedFrom)
                                                            .map(acc -> savedOut)
                                            )
                                            .onErrorResumeNext(err ->
                                                    rollbackMovement(savedOut)
                                                            .flatMap(ignored -> Single.error(err))
                                            )
                            );
                });
    }

    private Single<Movement> applyTransferIn(BankAccount toAcc, Double amount) {

        double newBalance = toAcc.getBalance() + amount;

        BankAccount updatedTo = new BankAccount(
                toAcc.getId(),
                toAcc.getCustomerId(),
                toAcc.getType(),
                newBalance,
                toAcc.getMaintenanceFee(),
                toAcc.getMovementCount() + 1,
                toAcc.getFreeMovements(),
                toAcc.getMovementCommission()
        );

        Movement movIn = Movement.builder()
                .productId(toAcc.getId())
                .type(MovementType.TRANSFER_IN)
                .amount(amount)
                .commission(0.0)
                .description("Transfer from " + toAcc.getId())
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(movIn)
                .flatMap(saved ->
                        accountClient.updateAccount(updatedTo)
                                .map(acc -> saved)
                );
    }

    private Single<Boolean> rollbackMovement(Movement mov) {
        return repository.deleteById(mov.getId())
                .andThen(Single.just(true));
    }
}