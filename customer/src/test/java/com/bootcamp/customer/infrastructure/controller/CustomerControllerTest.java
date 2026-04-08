package com.bootcamp.customer.infrastructure.controller;

import com.bootcamp.customer.application.service.CustomerApplicationService;
import com.bootcamp.customer.domain.model.CustomerType;
import com.bootcamp.customer.domain.model.PersonalCustomer;
import com.bootcamp.customer.domain.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerApplicationService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateCustomer() throws Exception {

        Customer customer = new PersonalCustomer();
        customer.setId("1");
        customer.setName("John");
        customer.setEmail("john@test.com");
        customer.setType(CustomerType.PERSONAL);

        when(service.create(any(Customer.class)))
                .thenReturn(Single.just(customer));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnAllCustomers() throws Exception {

        Customer c1 = new PersonalCustomer();
        c1.setId("1");

        Customer c2 = new PersonalCustomer();
        c2.setId("2");

        when(service.findAll())
                .thenReturn(Single.just(List.of(c1, c2)));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindCustomerById() throws Exception {

        Customer customer = new PersonalCustomer();
        customer.setId("1");

        when(service.findById("1"))
                .thenReturn(Maybe.just(customer));

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCustomer() throws Exception {

        Customer customer = new PersonalCustomer();
        customer.setId("1");
        customer.setName("John Updated");
        customer.setType(CustomerType.PERSONAL);

        when(service.update(eq("1"), any(Customer.class)))
                .thenReturn(Single.just(customer));

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCustomer() throws Exception {

        when(service.delete("1"))
                .thenReturn(Single.just(true).ignoreElement());

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isOk());
    }
}