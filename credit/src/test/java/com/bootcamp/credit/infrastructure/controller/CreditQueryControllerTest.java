package com.bootcamp.credit.infrastructure.controller;

import com.bootcamp.credit.application.service.CreditQueryService;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditQueryController.class)
class CreditQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditQueryService service;

    @Test
    void shouldReturnOkWhenCustomerHasCreditCard() throws Exception {

        when(service.hasCreditCard("c1"))
                .thenReturn(Single.just(true));

        mockMvc.perform(get("/api/v1/credits/card/exists/c1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWhenCustomerHasNoCreditCard() throws Exception {

        when(service.hasCreditCard("c2"))
                .thenReturn(Single.just(false));

        mockMvc.perform(get("/api/v1/credits/card/exists/c2"))
                .andExpect(status().isOk());
    }
}