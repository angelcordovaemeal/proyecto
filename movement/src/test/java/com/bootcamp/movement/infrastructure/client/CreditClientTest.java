package com.bootcamp.movement.infrastructure.client;

import com.bootcamp.movement.application.exception.BusinessException;
import com.bootcamp.movement.domain.model.response.Credit;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditClientTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Call call;

    @InjectMocks
    private CreditClient creditClient;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();

        ReflectionTestUtils.setField(creditClient, "client", okHttpClient);
        ReflectionTestUtils.setField(creditClient, "mapper", mapper);
        ReflectionTestUtils.setField(creditClient, "baseUrl", "http://localhost:8082/credits");
    }

    @Test
    void shouldReturnAllCredits() throws Exception {

        Credit credit = buildValidCredit("cr1");

        SuccessResponseWrapper<List<Credit>> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(List.of(credit));

        String json = mapper.writeValueAsString(wrapper);
        Response response = buildResponse(200, json, "/credits");

        mockCallWithResponse(response);

        creditClient.findAll()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFailWhenFindAllReturnsHttpError() {

        Response response = buildResponse(500, "", "/credits");

        mockCallWithResponse(response);

        creditClient.findAll()
                .test()
                .assertError(RuntimeException.class);
    }


    @Test
    void shouldReturnCreditWhenGetCreditIsSuccessful() throws Exception {

        Credit credit = buildValidCredit("cr1");

        SuccessResponseWrapper<Credit> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(credit);

        String json = mapper.writeValueAsString(wrapper);
        Response response = buildResponse(200, json, "/credits/cr1");

        mockCallWithResponse(response);

        creditClient.getCredit("cr1")
                .test()
                .assertComplete()
                .assertValue(c -> c.getId().equals("cr1"));
    }

    @Test
    void shouldFailWhenCreditNotFound() {

        Response response = buildResponse(404, "", "/credits/cr1");

        mockCallWithResponse(response);

        creditClient.getCredit("cr1")
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals("Credit not found")
                );
    }


    @Test
    void shouldUpdateCreditSuccessfully() throws Exception {

        Credit credit = buildValidCredit("cr1");

        SuccessResponseWrapper<Credit> wrapper = new SuccessResponseWrapper<>();
        wrapper.setData(credit);

        String json = mapper.writeValueAsString(wrapper);
        Response response = buildResponse(200, json, "/credits/cr1");

        mockCallWithResponse(response);

        creditClient.updateCredit(credit)
                .test()
                .assertComplete()
                .assertValue(c -> c.getId().equals("cr1"));
    }

    @Test
    void shouldFailWhenUpdateCreditReturnsError() {

        Credit credit = buildValidCredit("cr1");

        Response response = buildResponse(500, "", "/credits/cr1");

        mockCallWithResponse(response);

        creditClient.updateCredit(credit)
                .test()
                .assertError(e ->
                        e instanceof BusinessException &&
                                e.getMessage().equals("Failed to update credit")
                );
    }


    private Credit buildValidCredit(String id) {
        Credit credit = new Credit();
        credit.setId(id);
        credit.setCreditLimit(1000.0);
        credit.setUsedAmount(200.0);
        credit.setAvailableBalance(800.0);
        return credit;
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
                        .url("http://localhost:8082" + path)
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("OK")
                .body(body)
                .build();
    }
}