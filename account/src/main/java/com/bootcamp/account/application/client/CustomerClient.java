package com.bootcamp.account.application.client;

import com.bootcamp.account.domain.model.response.Customer;
import com.bootcamp.account.domain.model.response.SuccessResponseWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerClient {

    private final OkHttpClient client;

    @Value("${customer.service.url}")
    private String baseUrl;

    public Single<Customer> getCustomer(String id) {

        return Single.create(emitter -> {

            Request request = new Request.Builder()
                    .url(baseUrl + "/" + id)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(new RuntimeException("Error calling customer service: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        emitter.onError(new RuntimeException("Customer service error: " + response.code()));
                        return;
                    }

                    String body = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();

                    SuccessResponseWrapper<Customer> wrapper =
                            mapper.readValue(body, new TypeReference<SuccessResponseWrapper<Customer>>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }
}