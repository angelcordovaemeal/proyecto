package com.bootcamp.movement.infrastructure.client;

import com.bootcamp.movement.application.exception.BusinessException;
import com.bootcamp.movement.domain.model.response.BankAccount;
import com.bootcamp.movement.domain.model.response.SuccessResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.observers.TestObserver;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountClientTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call call;

    @InjectMocks
    private AccountClient accountClient;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();

        ReflectionTestUtils.setField(accountClient, "client", okHttpClient);
        ReflectionTestUtils.setField(accountClient, "mapper", mapper);
        ReflectionTestUtils.setField(accountClient, "baseUrl", "http://localhost:8080/accounts");
    }

    @Test
    void shouldReturnAllAccounts() throws Exception {

        BankAccount acc = new BankAccount();
        acc.setId("a1");

        SuccessResponseWrapper<List<BankAccount>> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(List.of(acc));

        String json = mapper.writeValueAsString(wrapper);
        Response response = buildResponse(200, json, "/accounts");

        mockCallWithResponse(response);

        TestObserver<List<BankAccount>> observer =
                accountClient.findAll().test();

        observer.assertComplete();
        observer.assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFailWhenFindAllReturnsHttpError() {

        Response response = buildResponse(500, "", "/accounts");

        mockCallWithResponse(response);

        accountClient.findAll()
                .test()
                .assertError(RuntimeException.class);
    }


    @Test
    void shouldReturnAccountWhenGetAccountIsSuccessful() throws Exception {

        BankAccount acc = new BankAccount();
        acc.setId("a1");

        SuccessResponseWrapper<BankAccount> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(acc);

        String json = mapper.writeValueAsString(wrapper);
        Response response = buildResponse(200, json, "/accounts/a1");

        mockCallWithResponse(response);

        accountClient.getAccount("a1")
                .test()
                .assertComplete()
                .assertValue(a -> a.getId().equals("a1"));
    }

    @Test
    void shouldFailWhenAccountNotFound() {

        Response response = buildResponse(404, "", "/accounts/a1");

        mockCallWithResponse(response);

        accountClient.getAccount("a1")
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals("Account not found")
                );
    }


    @Test
    void shouldUpdateAccountSuccessfully() throws Exception {

        BankAccount acc = new BankAccount();
        acc.setId("a1");

        SuccessResponseWrapper<BankAccount> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(acc);

        String json = mapper.writeValueAsString(wrapper);
        Response response = buildResponse(200, json, "/accounts/a1");

        mockCallWithResponse(response);

        accountClient.updateAccount(acc)
                .test()
                .assertComplete()
                .assertValue(a -> a.getId().equals("a1"));
    }

    @Test
    void shouldFailWhenUpdateAccountReturnsError() {

        BankAccount acc = new BankAccount();
        acc.setId("a1");

        Response response = buildResponse(500, "", "/accounts/a1");

        mockCallWithResponse(response);

        accountClient.updateAccount(acc)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals("Failed to update account")
                );
    }


    private void mockCallWithResponse(Response response) {

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));
    }

    private Response buildResponse(int code, String json, String path) {

        ResponseBody body = ResponseBody.create(
                json,
                MediaType.get("application/json")
        );

        return new Response.Builder()
                .request(new Request.Builder()
                        .url("http://localhost:8080" + path)
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("OK")
                .body(body)
                .build();
    }
}