package com.bootcamp.account.infrastructure.repository;

import com.bootcamp.account.domain.model.BankAccount;
import com.bootcamp.account.domain.model.SavingsAccount;
import com.bootcamp.account.domain.repository.AccountRepository;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImplTest {

    @Mock
    private AccountMongoRepository mongoRepository;

    @InjectMocks
    private AccountRepositoryImpl repository;

    private BankAccount account;

    @BeforeEach
    void setup() {
        account = new SavingsAccount();
        account.setId("1");
        account.setCustomerId("C1");
    }

    @Test
    void shouldSaveAccount() {

        when(mongoRepository.save(account))
                .thenReturn(account);

        repository.save(account)
                .test()
                .assertComplete()
                .assertValue(saved -> saved.getId().equals("1"));
    }

    @Test
    void shouldFindAllAccounts() {

        when(mongoRepository.findAll())
                .thenReturn(List.of(account));

        repository.findAll()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFindAccountByIdWhenExists() {

        when(mongoRepository.findById("1"))
                .thenReturn(Optional.of(account));

        repository.findById("1")
                .test()
                .assertComplete()
                .assertValue(found -> found.getId().equals("1"));
    }

    @Test
    void shouldReturnEmptyWhenAccountNotFoundById() {

        when(mongoRepository.findById("1"))
                .thenReturn(Optional.empty());

        repository.findById("1")
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @Test
    void shouldDeleteAccountById() {

        doNothing().when(mongoRepository).deleteById("1");

        repository.deleteById("1")
                .test()
                .assertComplete();

        verify(mongoRepository, times(1)).deleteById("1");
    }

    @Test
    void shouldFindAccountsByCustomerId() {

        when(mongoRepository.findByCustomerId("C1"))
                .thenReturn(List.of(account));

        repository.findByCustomerId("C1")
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }
}
