package com.bootcamp.movement.infrastructure.client;

import com.bootcamp.movement.application.exception.BusinessException;
import com.bootcamp.movement.domain.model.response.Credit;
import com.bootcamp.movement.domain.model.response.SuccessResponseWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreditClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    @Value("${credit.service.url}")
    private String baseUrl;

    public Single<List<Credit>> findAll() {

        return Single.create(emitter -> {

            Request request = new Request.Builder()
                    .url(baseUrl)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(new RuntimeException("Error calling credit service: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        emitter.onError(new RuntimeException("Credit service error: " + response.code()));
                        return;
                    }

                    String body = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();

                    SuccessResponseWrapper<List<Credit>> wrapper =
                            mapper.readValue(body, new TypeReference<SuccessResponseWrapper<List<Credit>>>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }

    public Single<Credit> getCredit(String id) {

        return Single.create(emitter -> {

            Request request = new Request.Builder()
                    .url(baseUrl + "/" + id)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.code() != 200) {
                        emitter.onError(new BusinessException("Credit not found"));
                        return;
                    }

                    String body = response.body().string();

                    SuccessResponseWrapper<Credit> wrapper =
                            mapper.readValue(body, new TypeReference<>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }

    public Single<Credit> updateCredit(Credit updated) {

        return Single.create(emitter -> {

            String json = mapper.writeValueAsString(updated);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + "/" + updated.getId())
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.code() != 200) {
                        emitter.onError(new BusinessException("Failed to update credit"));
                        return;
                    }

                    String raw = response.body().string();

                    SuccessResponseWrapper<Credit> wrapper =
                            mapper.readValue(raw, new TypeReference<>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }
}

