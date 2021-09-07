package com.xxxxxxh.http;

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class RetrofitUtils {
    private static final String BASE_URL = "http://smallfun.xyz/worldweather361/";
    private static RetrofitUtils instance = null;

    private RetrofitUtils() {
    }

    public static RetrofitUtils getInstance() {
        if (instance == null) {
            instance = new RetrofitUtils();
        }
        return instance;
    }

    public Retrofit retrofit() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String url = request.url().toString();
                return chain.proceed(request);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create())
                .build();
        return retrofit;
    }
}
