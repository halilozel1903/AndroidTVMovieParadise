package com.halil.ozel.movieparadise.data.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Appends the TMDB {@code api_key} query parameter to every request, so individual
 * API methods do not have to carry the key around.
 */
public final class ApiKeyInterceptor implements Interceptor {

    public static final String API_KEY_PARAM = "api_key";

    private final String apiKey;

    public ApiKeyInterceptor(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        if (original.url().queryParameter(API_KEY_PARAM) != null) {
            return chain.proceed(original);
        }
        HttpUrl url = original.url().newBuilder()
                .addQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        return chain.proceed(original.newBuilder().url(url).build());
    }
}
