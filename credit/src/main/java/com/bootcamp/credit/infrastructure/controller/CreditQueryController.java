package com.bootcamp.credit.infrastructure.controller;

import com.bootcamp.credit.application.messages.AppMessages;
import com.bootcamp.credit.application.service.CreditQueryService;
import com.bootcamp.credit.domain.model.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credits")
@RequiredArgsConstructor
public class CreditQueryController {

    private final CreditQueryService service;

    @GetMapping("/card/exists/{customerId}")
    public ResponseEntity<SuccessResponse<Boolean>> hasCreditCard(
            @PathVariable String customerId,
            HttpServletRequest req) {

        return service.hasCreditCard(customerId)
                .map(result ->
                        ResponseEntity.ok(
                                SuccessResponse.of(
                                        result,
                                        AppMessages.CREDIT_CARD_QUERY,
                                        req.getRequestURI(),
                                        HttpStatus.OK.value()
                                )
                        )
                )
                .blockingGet();
    }
}