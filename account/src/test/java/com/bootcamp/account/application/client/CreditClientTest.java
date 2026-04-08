package com.bootcamp.account.application.client;

import com.bootcamp.account.domain.model.response.SuccessResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.observers.TestObserver;
import okhttp3.*;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class CreditClientTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call call;

    private ObjectMapper mapper;

    @InjectMocks
    private CreditClient creditClient;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        ReflectionTestUtils.setField(creditClient, "mapper", mapper);
        ReflectionTestUtils.setField(creditClient, "creditUrl", "http://localhost:8083");
    }

    @Test
    void shouldReturnTrueWhenCustomerHasCreditCard() throws Exception {

        SuccessResponseWrapper<Boolean> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(true);

        String json = mapper.writeValueAsString(wrapper);

        Response response = buildOkResponse(json);

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        TestObserver<Boolean> observer =
                creditClient.hasCreditCard("123").test();

        observer.assertComplete();
        observer.assertValue(true);
    }

    @Test
    void shouldReturnFalseWhenCustomerHasNoCreditCard() throws Exception {

        SuccessResponseWrapper<Boolean> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(false);

        String json = mapper.writeValueAsString(wrapper);

        Response response = buildOkResponse(json);

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        creditClient.hasCreditCard("123")
                .test()
                .assertComplete()
                .assertValue(false);
    }

    @Test
    void shouldEmitErrorWhenHttpCallFails() {

        IOException error = new IOException("Connection error");

        when(okHttpClient.newCall(any(Request.class)))
                .thenReturn(call);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(call, error);
            return null;
        }).when(call).enqueue(any(Callback.class));

        creditClient.hasCreditCard("123")
                .test()
                .assertError(IOException.class);
    }

    private Response buildOkResponse(String json) {

        ResponseBody body = ResponseBody.create(
                json,
                MediaType.get("application/json")
        );

        return new Response.Builder()
                .request(new Request.Builder()
                        .url("http://localhost:8083/card/exists/123")
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(body)
                .build();
    }
}