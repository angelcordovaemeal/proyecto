package com.bootcamp.account.application.client;

import com.bootcamp.account.domain.model.response.Customer;
import com.bootcamp.account.domain.model.response.SuccessResponseWrapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class CustomerClientTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call call;

    @InjectMocks
    private CustomerClient customerClient;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        ReflectionTestUtils.setField(customerClient, "baseUrl", "http://localhost:8081/customers");
    }

    @Test
    void shouldReturnCustomerWhenRequestIsSuccessful() throws Exception {

        Customer customer = new Customer();
        customer.setId("1");
        customer.setName("John");
        customer.setEmail("john@test.com");

        SuccessResponseWrapper<Customer> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(customer);

        String json = mapper.writeValueAsString(wrapper);

        Response response = buildOkResponse(json, 200);

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        TestObserver<Customer> observer =
                customerClient.getCustomer("1").test();

        observer.assertComplete();
        observer.assertValue(c -> c.getId().equals("1"));
        observer.assertValue(c -> c.getName().equals("John"));
        observer.assertValue(c -> c.getEmail().equals("john@test.com"));
    }

    @Test
    void shouldEmitErrorWhenResponseIsNotSuccessful() {

        Response response = buildOkResponse("", 500);

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        customerClient.getCustomer("1")
                .test()
                .assertError(RuntimeException.class);
    }

    @Test
    void shouldEmitErrorWhenHttpCallFails() {

        IOException exception = new IOException("Connection refused");

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, exception);
            return null;
        }).when(call).enqueue(any(Callback.class));

        customerClient.getCustomer("1")
                .test()
                .assertError(RuntimeException.class);
    }

    private Response buildOkResponse(String json, int code) {

        ResponseBody body = ResponseBody.create(
                json,
                MediaType.get("application/json")
        );

        return new Response.Builder()
                .request(
                        new Request.Builder()
                                .url("http://localhost:8081/customers/1")
                                .build()
                )
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("OK")
                .body(body)
                .build();
    }
}