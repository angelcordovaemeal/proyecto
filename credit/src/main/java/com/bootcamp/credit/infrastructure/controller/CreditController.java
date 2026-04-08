package com.bootcamp.credit.infrastructure.controller;

import com.bootcamp.credit.application.messages.AppMessages;
import com.bootcamp.credit.application.service.CreditApplicationService;
import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.domain.model.response.SuccessResponse;
import io.reactivex.rxjava3.core.Completable;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditApplicationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<Credit>> create(
            @RequestBody Credit credit,
            HttpServletRequest request) {

        return service.createCredit(credit)
                .map(c -> SuccessResponse.of(
                        c,
                        AppMessages.CREDIT_CREATED,
                        request.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @GetMapping("/{id}")
    public Maybe<SuccessResponse<Credit>> findById(
            @PathVariable String id,
            HttpServletRequest request) {

        return service.findById(id)
                .map(c -> SuccessResponse.of(
                        c,
                        AppMessages.CREDIT_FOUND,
                        request.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @GetMapping
    public Single<SuccessResponse<List<Credit>>> findAll(HttpServletRequest request) {

        return service.findAll()
                .toList()
                .map(list -> SuccessResponse.of(
                        list,
                        AppMessages.CREDITS_RETRIEVED,
                        request.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @GetMapping("/customer/{customerId}")
    public Single<SuccessResponse<List<Credit>>> findByCustomerId(
            @PathVariable String customerId,
            HttpServletRequest request) {

        return service.findByCustomerId(customerId)
                .toList()
                .map(list -> SuccessResponse.of(
                        list,
                        AppMessages.CREDITS_BY_CUSTOMER_RETRIEVED,
                        request.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @PutMapping("/{id}")
    public Single<SuccessResponse<Credit>> update(
            @PathVariable String id,
            @RequestBody Credit credit,
            HttpServletRequest request) {

        return service.updateCredit(id, credit)
                .map(c -> SuccessResponse.of(
                        c,
                        AppMessages.CREDIT_UPDATED,
                        request.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Completable delete(@PathVariable String id) {
        return service.deleteById(id);
    }
}