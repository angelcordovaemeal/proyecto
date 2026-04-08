package com.bootcamp.account.infrastructure.controller;

import com.bootcamp.account.application.messages.AppMessages;
import com.bootcamp.account.application.service.AccountApplicationService;
import com.bootcamp.account.domain.model.BankAccount;
import com.bootcamp.account.domain.model.response.SuccessResponse;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountApplicationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<BankAccount>> create(
            @RequestBody BankAccount account,
            HttpServletRequest req) {

        return service.create(account)
                .map(saved -> SuccessResponse.of(
                        saved,
                        AppMessages.ACCOUNT_CREATED,
                        req.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @PostMapping("/apply-movement/{id}")
    public Single<SuccessResponse<BankAccount>> applyMovement(
            @PathVariable String id,
            @RequestParam Double amount,
            @RequestParam Double commission,
            HttpServletRequest req) {

        return service.applyMovement(id, amount, commission)
                .map(acc -> SuccessResponse.of(
                        acc,
                        AppMessages.MOVEMENT_APPLIED,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @GetMapping
    public Single<SuccessResponse<List<BankAccount>>> findAll(
            HttpServletRequest req) {

        return service.findAll()
                .map(list -> SuccessResponse.of(
                        list,
                        AppMessages.ACCOUNT_RETRIEVED,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @GetMapping("/{id}")
    public Maybe<SuccessResponse<BankAccount>> findById(
            @PathVariable String id,
            HttpServletRequest req) {

        return service.findById(id)
                .map(found -> SuccessResponse.of(
                        found,
                        AppMessages.ACCOUNT_FOUND,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @PutMapping("/{id}")
    public Single<SuccessResponse<BankAccount>> update(
            @PathVariable String id,
            @RequestBody BankAccount account,
            HttpServletRequest req) {

        return service.update(id, account)
                .map(updated -> SuccessResponse.of(
                        updated,
                        AppMessages.ACCOUNT_UPDATED,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<SuccessResponse<Void>> delete(
            @PathVariable String id,
            HttpServletRequest req) {

        return service.delete(id)
                .andThen(
                        Single.just(
                                SuccessResponse.of(
                                        null,
                                        AppMessages.ACCOUNT_DELETED,
                                        req.getRequestURI(),
                                        HttpStatus.OK.value()
                                )
                        )
                );
    }
}