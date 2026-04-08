package com.bootcamp.customer.application.service;

import com.bootcamp.customer.application.messages.AppMessages;
import com.bootcamp.customer.domain.model.Customer;
import com.bootcamp.customer.domain.model.PersonalCustomer;
import com.bootcamp.customer.domain.repository.CustomerRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerApplicationServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerApplicationService service;

    private Customer customer;

    @BeforeEach
    void setup() {
        customer = new PersonalCustomer();
        customer.setId("1");
        customer.setName("John");
        customer.setEmail("john@test.com");
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        when(repository.save(customer)).thenReturn(Single.just(customer));

        service.create(customer)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(c -> c.getId().equals("1"));
    }

    @Test
    void shouldReturnAllCustomers() {
        when(repository.findAll()).thenReturn(Single.just(List.of(customer)));

        service.findAll()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFindCustomerById() {
        when(repository.findById("1")).thenReturn(Maybe.just(customer));

        service.findById("1")
                .test()
                .assertComplete()
                .assertValue(c -> c.getId().equals("1"));
    }

    @Test
    void shouldFailWhenCustomerNotFoundById() {
        when(repository.findById("99")).thenReturn(Maybe.empty());

        service.findById("99")
                .test()
                .assertError(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals(AppMessages.CUSTOMER_NOT_FOUND)
                );
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        when(repository.findById("1")).thenReturn(Maybe.just(customer));
        when(repository.save(any(Customer.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        Customer updated = new PersonalCustomer();
        updated.setName("Jane");

        service.update("1", updated)
                .test()
                .assertComplete()
                .assertValue(c -> c.getId().equals("1"));
    }

    @Test
    void shouldFailUpdateWhenCustomerNotFound() {
        when(repository.findById("99")).thenReturn(Maybe.empty());

        service.update("99", customer)
                .test()
                .assertError(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals(AppMessages.CUSTOMER_NOT_FOUND)
                );
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        when(repository.findById("1")).thenReturn(Maybe.just(customer));
        when(repository.deleteById("1")).thenReturn(Maybe.empty().ignoreElement());

        service.delete("1")
                .test()
                .assertComplete()
                .assertNoErrors();
    }

    @Test
    void shouldFailDeleteWhenCustomerNotFound() {
        when(repository.findById("99")).thenReturn(Maybe.empty());

        service.delete("99")
                .test()
                .assertError(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals(AppMessages.CUSTOMER_NOT_FOUND)
                );
    }
}