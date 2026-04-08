package com.bootcamp.account.infrastructure.controller;

import com.bootcamp.account.application.messages.AppMessages;
import com.bootcamp.account.application.service.AccountApplicationService;
import com.bootcamp.account.domain.model.AccountType;
import com.bootcamp.account.domain.model.BankAccount;
import com.bootcamp.account.domain.model.SavingsAccount;
import com.bootcamp.account.infrastructure.controller.AccountController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountApplicationService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateAccount() throws Exception {

        BankAccount account = new SavingsAccount();
        account.setId("1");
        account.setCustomerId("1");
        account.setType(AccountType.SAVINGS);

        when(service.create(any(BankAccount.class)))
                .thenReturn(Single.just(account));

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldApplyMovement() throws Exception {

        BankAccount account = new SavingsAccount();
        account.setId("1");

        when(service.applyMovement("1", 100.0, 0.0))
                .thenReturn(Single.just(account));

        mockMvc.perform(post("/api/v1/accounts/apply-movement/{id}", "1")
                        .queryParam("amount", "100")
                        .queryParam("commission", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAllAccounts() throws Exception {

        BankAccount acc1 = new SavingsAccount();
        acc1.setId("1");
        BankAccount acc2 = new SavingsAccount();
        acc2.setId("2");

        when(service.findAll())
                .thenReturn(Single.just(List.of(acc1, acc2)));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAccountById() throws Exception {

        BankAccount account = new SavingsAccount();
        account.setId("1");

        when(service.findById("1"))
                .thenReturn(Maybe.just(account));

        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateAccount() throws Exception {

        BankAccount account = new SavingsAccount();
        account.setId("1");
        account.setType(AccountType.SAVINGS);

        when(service.update(eq("1"), any(BankAccount.class)))
                .thenReturn(Single.just(account));

        mockMvc.perform(put("/api/v1/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteAccount() throws Exception {

        when(service.delete("1"))
                .thenReturn(Single.just(true).ignoreElement());

        mockMvc.perform(delete("/api/v1/accounts/1"))
                .andExpect(status().isOk());
    }
}