package com.bootcamp.movement.infrastructure.controller;

import com.bootcamp.movement.application.messages.AppMessages;
import com.bootcamp.movement.application.service.MovementApplicationService;
import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.TransferRequest;
import com.bootcamp.movement.domain.model.response.SuccessResponse;
import io.reactivex.rxjava3.core.Single;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementApplicationService service;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<Movement>> deposit(
            @RequestBody Movement movement,
            HttpServletRequest req) {

        return service.registerDeposit(movement)
                .map(m -> SuccessResponse.of(
                        m,
                        AppMessages.DEPOSIT_REGISTERED,
                        req.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<Movement>> withdraw(
            @RequestBody Movement movement,
            HttpServletRequest req) {

        return service.registerWithdrawal(movement)
                .map(m -> SuccessResponse.of(
                        m,
                        AppMessages.WITHDRAWAL_REGISTERED,
                        req.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @PostMapping("/credit/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<Movement>> creditPayment(
            @RequestBody Movement movement,
            HttpServletRequest req) {

        return service.registerCreditPayment(movement)
                .map(m -> SuccessResponse.of(
                        m,
                        AppMessages.CREDIT_PAYMENT_REGISTERED,
                        req.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @PostMapping("/credit/consumption")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<Movement>> creditConsumption(
            @RequestBody Movement movement,
            HttpServletRequest req) {

        return service.registerCreditConsumption(movement)
                .map(m -> SuccessResponse.of(
                        m,
                        AppMessages.CREDIT_CONSUMPTION_REGISTERED,
                        req.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @GetMapping("/{productId}")
    public Single<SuccessResponse<List<Movement>>> findByProduct(
            @PathVariable String productId,
            HttpServletRequest req) {

        return service.findByProduct(productId)
                .map(list -> SuccessResponse.of(
                        list,
                        AppMessages.MOVEMENTS_RETRIEVED,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @GetMapping
    public Single<SuccessResponse<List<Movement>>> findAll(
            HttpServletRequest req) {

        return service.findAll()
                .map(list -> SuccessResponse.of(
                        list,
                        AppMessages.MOVEMENTS_RETRIEVED,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @PostMapping("/transfer")
    public Single<ResponseEntity<SuccessResponse<Movement>>> transfer(
            @RequestBody TransferRequest request,
            HttpServletRequest req) {

        return service.transfer(request)
                .map(mov -> ResponseEntity.ok(
                        SuccessResponse.of(
                                mov,
                                AppMessages.TRANSFER_PROCESSED,
                                req.getRequestURI(),
                                HttpStatus.OK.value()
                        )
                ));
    }
}