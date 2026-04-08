package com.bootcamp.credit.infrastructure.controller;

import com.bootcamp.credit.application.service.CreditApplicationService;
import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.domain.model.CreditType;
import com.bootcamp.credit.domain.model.PersonalCredit;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditController.class)
class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditApplicationService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateCredit() throws Exception {

        Credit credit = new PersonalCredit();
        credit.setId("cr1");
        credit.setCustomerId("c1");
        credit.setType(CreditType.PERSONAL);

        when(service.createCredit(any(Credit.class)))
                .thenReturn(Single.just(credit));

        mockMvc.perform(post("/api/v1/credits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credit)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFindCreditById() throws Exception {

        Credit credit = new PersonalCredit();
        credit.setId("cr1");
        credit.setType(CreditType.PERSONAL);

        when(service.findById("cr1"))
                .thenReturn(Maybe.just(credit));

        mockMvc.perform(get("/api/v1/credits/cr1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAllCredits() throws Exception {

        Credit c1 = new PersonalCredit();
        c1.setId("cr1");
        c1.setType(CreditType.PERSONAL);

        Credit c2 = new PersonalCredit();
        c2.setId("cr2");
        c2.setType(CreditType.PERSONAL);

        when(service.findAll())
                .thenReturn(Flowable.fromIterable(List.of(c1, c2)));

        mockMvc.perform(get("/api/v1/credits"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCreditsByCustomerId() throws Exception {

        Credit credit = new PersonalCredit();
        credit.setId("cr1");
        credit.setCustomerId("c1");
        credit.setType(CreditType.PERSONAL);

        when(service.findByCustomerId("c1"))
                .thenReturn(Flowable.just(credit));

        mockMvc.perform(get("/api/v1/credits/customer/c1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCredit() throws Exception {

        Credit credit = new PersonalCredit();
        credit.setId("cr1");
        credit.setType(CreditType.PERSONAL);

        when(service.updateCredit(eq("cr1"), any(Credit.class)))
                .thenReturn(Single.just(credit));

        mockMvc.perform(put("/api/v1/credits/cr1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credit)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCredit() throws Exception {

        when(service.deleteById("cr1"))
                .thenReturn(Completable.complete());

        mockMvc.perform(delete("/api/v1/credits/cr1"))
                .andExpect(status().isNoContent());
    }
}