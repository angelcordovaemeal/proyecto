package com.bootcamp.credit.infrastructure.client;

import com.bootcamp.credit.application.exception.BusinessException;
import com.bootcamp.credit.domain.model.response.Customer;
import com.bootcamp.credit.domain.model.response.SuccessResponseWrapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        ReflectionTestUtils.setField(customerClient, "mapper", mapper);
        ReflectionTestUtils.setField(customerClient, "client", okHttpClient);
        ReflectionTestUtils.setField(customerClient, "baseUrl", "http://localhost:8081/customers");
    }

    @Test
    void shouldReturnCustomerWhenResponseIs200() throws Exception {

        Customer customer = new Customer();
        customer.setId("1");

        SuccessResponseWrapper<Customer> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(customer);

        String json = mapper.writeValueAsString(wrapper);

        Response response = buildResponse(200, json);

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        TestObserver<Customer> observer =
                customerClient.getCustomerById("1").test();

        observer.assertComplete();
        observer.assertValue(c -> c.getId().equals("1"));
    }

    @Test
    void shouldFailWhenResponseIsNot200() {

        Response response = buildResponse(404, "");

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        customerClient.getCustomerById("1")
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals("Customer not found")
                );
    }

    @Test
    void shouldFailWhenHttpCallFails() {

        IOException exception = new IOException("Connection error");

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, exception);
            return null;
        }).when(call).enqueue(any(Callback.class));

        customerClient.getCustomerById("1")
                .test()
                .assertError(IOException.class);
    }

    private Response buildResponse(int code, String json) {

        ResponseBody body = ResponseBody.create(
                json,
                MediaType.get("application/json")
        );

        return new Response.Builder()
                .request(new Request.Builder()
                        .url("http://localhost:8081/customers/1")
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("OK")
                .body(body)
                .build();
    }
}