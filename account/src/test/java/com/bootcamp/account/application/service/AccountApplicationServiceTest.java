package com.bootcamp.account.application.service;

import com.bootcamp.account.application.client.CreditClient;
import com.bootcamp.account.application.client.CustomerClient;
import com.bootcamp.account.application.messages.AppMessages;
import com.bootcamp.account.domain.model.AccountType;
import com.bootcamp.account.domain.model.BankAccount;
import com.bootcamp.account.domain.model.CurrentAccount;
import com.bootcamp.account.domain.model.CustomerType;
import com.bootcamp.account.domain.model.FixedTermAccount;
import com.bootcamp.account.domain.model.SavingsAccount;
import com.bootcamp.account.domain.model.response.Customer;
import com.bootcamp.account.domain.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private AccountRepository repository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private CreditClient creditClient;

    @InjectMocks
    private AccountApplicationService service;

    private Customer customer;
    private Customer customerBusiness;
    private BankAccount savingsAccount;
    private BankAccount currentAccount;
    private BankAccount fixedTermAccount;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "vipMinAvg", 500.0);

        customer = new Customer();
        customer.setId("1");
        customer.setType(CustomerType.PERSONAL);
        customer.setProfile("STANDARD");

        customerBusiness = new Customer();
        customerBusiness.setId("2");
        customerBusiness.setType(CustomerType.BUSINESS);
        customerBusiness.setProfile("STANDARD");

        savingsAccount = new SavingsAccount(1);
        savingsAccount.setId("1a");
        savingsAccount.setCustomerId("1");
        savingsAccount.setType(AccountType.SAVINGS);
        savingsAccount.setBalance(0.0);

        currentAccount = new CurrentAccount();
        currentAccount.setId("2a");
        currentAccount.setCustomerId("2");
        currentAccount.setType(AccountType.CURRENT);
        currentAccount.setBalance(0.0);

        fixedTermAccount = new FixedTermAccount();
        fixedTermAccount.setId("3a");
        fixedTermAccount.setCustomerId("1");
        fixedTermAccount.setType(AccountType.FIXED_TERM);
        fixedTermAccount.setBalance(0.0);
    }

    @Test
    void shouldCreatePersonalSavingsAccountSuccessfully() {

        when(customerClient.getCustomer("1"))
                .thenReturn(Single.just(customer));

        when(repository.findByCustomerId("1"))
                .thenReturn(Single.just(List.of()));

        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        log.error("");

        service.create(savingsAccount)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(acc -> acc.getId().equals("1a"))
                .assertValue(acc -> acc.getCustomerId().equals("1"))
                .assertValue(acc -> acc.getType().equals(AccountType.SAVINGS))
                .assertValue(acc -> acc.getBalance().equals(0.0));

    }

    @Test
    void shouldFailWhenPersonalHasMoreThanOneSavingsAccount() {

        when(customerClient.getCustomer("1"))
                .thenReturn(Single.just(customer));

        when(repository.findByCustomerId("1"))
                .thenReturn(Single.just(List.of(savingsAccount)));

        service.create(savingsAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldFailWhenBusinessCreatesSavingsAccount() {

        savingsAccount.setCustomerId("2");

        when(customerClient.getCustomer("2"))
                .thenReturn(Single.just(customerBusiness));

        when(repository.findByCustomerId("2"))
                .thenReturn(Single.just(List.of()));

        service.create(savingsAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldFailVipWhenCustomerIsNotPersonal() {

        savingsAccount.setCustomerId("2");
        customerBusiness.setProfile("VIP");

        when(customerClient.getCustomer("2"))
                .thenReturn(Single.just(customerBusiness));

        when(repository.findByCustomerId("2"))
                .thenReturn(Single.just(List.of()));

        service.create(savingsAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldFailVipWhenAccountIsNotSavings() {

        customer.setProfile("VIP");

        currentAccount.setCustomerId("1");

        when(customerClient.getCustomer("1"))
                .thenReturn(Single.just(customer));

        when(repository.findByCustomerId("1"))
                .thenReturn(Single.just(List.of()));

        service.create(currentAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldFailVipWithoutCreditCard() {

        customer.setProfile("VIP");

        when(customerClient.getCustomer("1"))
                .thenReturn(Single.just(customer));

        when(repository.findByCustomerId("1"))
                .thenReturn(Single.just(List.of()));

        when(creditClient.hasCreditCard("1"))
                .thenReturn(Single.just(false));

        service.create(savingsAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldCreateVipSavingsAccountWhenCreditCardExists() {

        customer.setProfile("VIP");

        when(customerClient.getCustomer("1"))
                .thenReturn(Single.just(customer));

        when(repository.findByCustomerId("1"))
                .thenReturn(Single.just(List.of()));

        when(creditClient.hasCreditCard("1"))
                .thenReturn(Single.just(true));

        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        service.create(savingsAccount)
                .test()
                .assertComplete()
                .assertNoErrors();
    }

    @Test
    void shouldFailPymeWhenCustomerIsNotBusiness() {

        customer.setProfile("PYME");

        currentAccount.setCustomerId("1");

        when(customerClient.getCustomer("1"))
                .thenReturn(Single.just(customer));

        when(repository.findByCustomerId("1"))
                .thenReturn(Single.just(List.of()));

        service.create(currentAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldFailPymeWhenAccountIsNotCurrent() {

        customerBusiness.setProfile("PYME");
        savingsAccount.setCustomerId("2");

        when(customerClient.getCustomer("2"))
                .thenReturn(Single.just(customerBusiness));

        when(repository.findByCustomerId("2"))
                .thenReturn(Single.just(List.of()));

        service.create(savingsAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldFailPymeWithoutCreditCard() {

        customerBusiness.setProfile("PYME");
        currentAccount.setCustomerId("2");

        when(customerClient.getCustomer("2"))
                .thenReturn(Single.just(customerBusiness));

        when(repository.findByCustomerId("2"))
                .thenReturn(Single.just(List.of()));

        when(creditClient.hasCreditCard("2"))
                .thenReturn(Single.just(false));

        service.create(currentAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldCreatePymeCurrentAccountWhenCreditCardExists() {

        customerBusiness.setProfile("PYME");
        currentAccount.setCustomerId("2");

        when(customerClient.getCustomer("2"))
                .thenReturn(Single.just(customerBusiness));

        when(repository.findByCustomerId("2"))
                .thenReturn(Single.just(List.of()));

        when(creditClient.hasCreditCard("2"))
                .thenReturn(Single.just(true));

        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        service.create(currentAccount)
                .test()
                .assertComplete()
                .assertNoErrors();
    }

    @Test
    void shouldReturnAllAccounts() {

        when(repository.findAll())
                .thenReturn(Single.just(List.of(
                        savingsAccount,
                        currentAccount,
                        fixedTermAccount
                )));

        service.findAll()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 3)
                .assertValue(list -> list.get(0).getId().equals("1a"))
                .assertValue(list -> list.get(0).getType().equals(AccountType.SAVINGS))
                .assertValue(list -> list.get(1).getId().equals("2a"))
                .assertValue(list -> list.get(1).getType().equals(AccountType.CURRENT))
                .assertValue(list -> list.get(2).getId().equals("3a"))
                .assertValue(list -> list.get(2).getType().equals(AccountType.FIXED_TERM));
    }

    @Test
    void shouldFindAccountById() {

        when(repository.findById("1a"))
                .thenReturn(Single.just(savingsAccount).toMaybe());

        service.findById("1a")
                .test()
                .assertComplete()
                .assertValue(acc -> acc.getId().equals("1a"));
    }

    @Test
    void shouldFailWhenAccountNotFoundById() {

        when(repository.findById("x"))
                .thenReturn(io.reactivex.rxjava3.core.Maybe.error(new RuntimeException(AppMessages.ACCOUNT_NOT_FOUND)));

        service.findById("x")
                .test()
                .assertError(RuntimeException.class);

    }

    @Test
    void shouldUpdateAccountSuccessfully() {

        when(repository.findById("1a"))
                .thenReturn(Single.just(savingsAccount).toMaybe());

        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        savingsAccount.setBalance(100.0);

        service.update("1a", savingsAccount)
                .test()
                .assertComplete()
                .assertValue(acc -> acc.getBalance().equals(100.0));
    }

    @Test
    void shouldFailWhenUpdatingNonExistingAccount() {

        when(repository.findById("x"))
                .thenReturn(io.reactivex.rxjava3.core.Maybe.error(new RuntimeException(AppMessages.ACCOUNT_NOT_FOUND)));

        service.update("x", savingsAccount)
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldDeleteAccountSuccessfully() {

        when(repository.findById("1a"))
                .thenReturn(Single.just(savingsAccount).toMaybe());

        when(repository.deleteById("1a"))
                .thenReturn(Single.just(true).ignoreElement());

        service.delete("1a")
                .test()
                .assertComplete();
    }

    @Test
    void shouldFailWhenDeletingNonExistingAccount() {

        when(repository.findById("x"))
                .thenReturn(io.reactivex.rxjava3.core.Maybe.error(new RuntimeException(AppMessages.ACCOUNT_NOT_FOUND)));

        service.delete("x")
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldApplyMovementSuccessfully() {

        savingsAccount.setBalance(100.0);
        savingsAccount.setMovementCount(0);

        when(repository.findById("1a"))
                .thenReturn(Single.just(savingsAccount).toMaybe());

        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

        service.applyMovement("1a", 50.0, 0.0)
                .test()
                .assertComplete()
                .assertValue(acc -> acc.getBalance().equals(150.0));
    }

    @Test
    void shouldFailApplyMovementWhenInsufficientFunds() {

        savingsAccount.setBalance(10.0);

        when(repository.findById("1a"))
                .thenReturn(Single.just(savingsAccount).toMaybe());

        service.applyMovement("1a", 50.0, 0.0)
                .test()
                .assertError(RuntimeException.class);
    }
}