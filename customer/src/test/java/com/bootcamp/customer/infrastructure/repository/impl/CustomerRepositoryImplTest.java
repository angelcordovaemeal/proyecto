package com.bootcamp.customer.infrastructure.repository.impl;

import com.bootcamp.customer.domain.model.Customer;
import com.bootcamp.customer.domain.model.PersonalCustomer;
import com.bootcamp.customer.infrastructure.repository.CustomerMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryImplTest {

    @Mock
    private CustomerMongoRepository mongoRepository;

    @InjectMocks
    private CustomerRepositoryImpl repository;

    private Customer customer;

    @BeforeEach
    void setup() {
        customer = new PersonalCustomer();
        customer.setId("1");
        customer.setName("John");
        customer.setEmail("john@test.com");
    }

    @Test
    void shouldSaveCustomer() {

        when(mongoRepository.save(customer))
                .thenReturn(customer);

        repository.save(customer)
                .test()
                .assertComplete()
                .assertValue(saved -> saved.getId().equals("1"));
    }

    @Test
    void shouldFindAllCustomers() {

        when(mongoRepository.findAll())
                .thenReturn(List.of(customer));

        repository.findAll()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFindCustomerByIdWhenExists() {

        when(mongoRepository.findById("1"))
                .thenReturn(Optional.of(customer));

        repository.findById("1")
                .test()
                .assertComplete()
                .assertValue(found -> found.getId().equals("1"));
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFoundById() {

        when(mongoRepository.findById("1"))
                .thenReturn(Optional.empty());

        repository.findById("1")
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @Test
    void shouldDeleteCustomerById() {

        doNothing().when(mongoRepository).deleteById("1");

        repository.deleteById("1")
                .test()
                .assertComplete();

        verify(mongoRepository, times(1)).deleteById("1");
    }
}