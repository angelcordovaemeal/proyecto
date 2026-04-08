package com.bootcamp.customer.infrastructure.controller;

import com.bootcamp.customer.application.messages.AppMessages;
import com.bootcamp.customer.application.service.CustomerApplicationService;
import com.bootcamp.customer.domain.model.Customer;
import com.bootcamp.customer.domain.model.response.SuccessResponse;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerApplicationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<SuccessResponse<Customer>> create(
            @RequestBody Customer customer,
            HttpServletRequest req) {

        return service.create(customer)
                .map(saved -> SuccessResponse.of(
                        saved,
                        AppMessages.CUSTOMER_CREATED,
                        req.getRequestURI(),
                        HttpStatus.CREATED.value()
                ));
    }

    @GetMapping
    public Single<SuccessResponse<List<Customer>>> findAll(
            HttpServletRequest req) {

        return service.findAll()
                .map(list -> SuccessResponse.of(
                        list,
                        AppMessages.CUSTOMER_RETRIEVED,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @GetMapping("/{id}")
    public Maybe<SuccessResponse<Customer>> findById(
            @PathVariable String id,
            HttpServletRequest req) {

        return service.findById(id)
                .map(found -> SuccessResponse.of(
                        found,
                        AppMessages.CUSTOMER_FOUND,
                        req.getRequestURI(),
                        HttpStatus.OK.value()
                ));
    }

    @PutMapping("/{id}")
    public Single<SuccessResponse<Customer>> update(
            @PathVariable String id,
            @RequestBody Customer customer,
            HttpServletRequest req) {

        return service.update(id, customer)
                .map(updated -> SuccessResponse.of(
                        updated,
                        AppMessages.CUSTOMER_UPDATED,
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
                                        AppMessages.CUSTOMER_DELETED,
                                        req.getRequestURI(),
                                        HttpStatus.OK.value()
                                )
                        )
                );
    }
}