package com.bootcamp.account.application.client;

import com.bootcamp.account.domain.model.response.SuccessResponseWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
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
public class CreditClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    @Value("${credit.service.url}")
    private String creditUrl;

    public Single<Boolean> hasCreditCard(String customerId) {
        return Single.create(emitter -> {
            Request request = new Request.Builder()
                    .url(creditUrl + "/card/exists/" + customerId)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();

                    SuccessResponseWrapper<Boolean> wrapper =
                            mapper.readValue(body, new TypeReference<>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }
}