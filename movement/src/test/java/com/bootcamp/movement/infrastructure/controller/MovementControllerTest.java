package com.bootcamp.movement.infrastructure.controller;

import com.bootcamp.movement.application.service.MovementApplicationService;
import com.bootcamp.movement.domain.model.Movement;
import com.bootcamp.movement.domain.model.TransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovementController.class)
class MovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovementApplicationService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldRegisterDeposit() throws Exception {

        Movement movement = new Movement();
        movement.setProductId("acc1");
        movement.setAmount(100.0);

        when(service.registerDeposit(any(Movement.class)))
                .thenReturn(Single.just(movement));

        mockMvc.perform(post("/api/v1/movements/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRegisterWithdrawal() throws Exception {

        Movement movement = new Movement();
        movement.setProductId("acc1");
        movement.setAmount(100.0);

        when(service.registerWithdrawal(any(Movement.class)))
                .thenReturn(Single.just(movement));

        mockMvc.perform(post("/api/v1/movements/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRegisterCreditPayment() throws Exception {

        Movement movement = new Movement();
        movement.setProductId("cr1");
        movement.setAmount(50.0);

        when(service.registerCreditPayment(any(Movement.class)))
                .thenReturn(Single.just(movement));

        mockMvc.perform(post("/api/v1/movements/credit/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRegisterCreditConsumption() throws Exception {

        Movement movement = new Movement();
        movement.setProductId("cr1");
        movement.setAmount(80.0);

        when(service.registerCreditConsumption(any(Movement.class)))
                .thenReturn(Single.just(movement));

        mockMvc.perform(post("/api/v1/movements/credit/consumption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFindMovementsByProduct() throws Exception {

        when(service.findByProduct("acc1"))
                .thenReturn(Single.just(List.of(new Movement())));

        mockMvc.perform(get("/api/v1/movements/acc1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllMovements() throws Exception {

        when(service.findAll())
                .thenReturn(Single.just(List.of(new Movement())));

        mockMvc.perform(get("/api/v1/movements"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldTransferSuccessfully() throws Exception {

        Movement movement = new Movement();
        movement.setProductId("acc1");

        TransferRequest request = new TransferRequest("acc1", "acc2", 100.0);

        when(service.transfer(any(TransferRequest.class)))
                .thenReturn(Single.just(movement));

        mockMvc.perform(post("/api/v1/movements/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}