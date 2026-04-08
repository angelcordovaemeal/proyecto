package com.bootcamp.movement.infrastructure.client;

import com.bootcamp.movement.application.exception.BusinessException;
import com.bootcamp.movement.domain.model.response.BankAccount;
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
public class AccountClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    @Value("${account.service.url}")
    private String baseUrl;

    public Single<List<BankAccount>> findAll() {

        return Single.create(emitter -> {

            Request request = new Request.Builder()
                    .url(baseUrl)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(new RuntimeException("Error calling account service: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        emitter.onError(new RuntimeException("Account service error: " + response.code()));
                        return;
                    }

                    String body = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();

                    SuccessResponseWrapper<List<BankAccount>> wrapper =
                            mapper.readValue(body, new TypeReference<SuccessResponseWrapper<List<BankAccount>>>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }

    public Single<BankAccount> getAccount(String id) {
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
                        emitter.onError(new BusinessException("Account not found"));
                        return;
                    }

                    String body = response.body().string();

                    SuccessResponseWrapper<BankAccount> wrapper =
                            mapper.readValue(body, new TypeReference<>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }

    public Single<BankAccount> updateAccount(BankAccount updatedAccount) {

        return Single.create(emitter -> {

            String json = mapper.writeValueAsString(updatedAccount);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + "/" + updatedAccount.getId())
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
                        emitter.onError(new BusinessException("Failed to update account"));
                        return;
                    }

                    String raw = response.body().string();

                    SuccessResponseWrapper<BankAccount> wrapper =
                            mapper.readValue(raw, new TypeReference<>() {});

                    emitter.onSuccess(wrapper.getData());
                }
            });
        });
    }

}
