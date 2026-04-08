package com.bootcamp.credit.infrastructure.client;

import com.bootcamp.credit.application.exception.BusinessException;
import com.bootcamp.credit.domain.model.response.Customer;
import com.bootcamp.credit.domain.model.response.SuccessResponseWrapper;
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
    private final ObjectMapper mapper;

    @Value("${customer.service.url}")
    private String baseUrl;

    public Single<Customer> getCustomerById(String id) {
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
                    if(response.code()!=200){
                        emitter.onError(new BusinessException("Customer not found"));
                    }else{
                        String body = response.body().string();
                        SuccessResponseWrapper<Customer> wrapper =
                                mapper.readValue(body, new TypeReference<>() {});

                        emitter.onSuccess(wrapper.getData());
                    }
                }
            });
        });
    }
}
