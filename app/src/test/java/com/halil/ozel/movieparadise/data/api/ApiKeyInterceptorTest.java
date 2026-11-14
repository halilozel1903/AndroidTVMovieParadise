package com.halil.ozel.movieparadise.data.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptorTest {

    private MockWebServer server;
    private OkHttpClient client;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInterceptor("test-key"))
                .build();
    }

    @After
    public void tearDown() {
        server.close();
    }

    private HttpUrl execute(String path) throws Exception {
        server.enqueue(new MockResponse.Builder().code(200).body("{}").build());
        Request request = new Request.Builder().url(server.url(path)).build();
        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code());
        }
        return server.takeRequest().getUrl();
    }

    @Test
    public void appendsApiKeyAndKeepsExistingParameters() throws Exception {
        HttpUrl url = execute("/3/movie/popular?page=2");

        assertEquals("test-key", url.queryParameter(ApiKeyInterceptor.API_KEY_PARAM));
        assertEquals("2", url.queryParameter("page"));
    }

    @Test
    public void doesNotDuplicateAnExplicitApiKey() throws Exception {
        HttpUrl url = execute("/3/movie/popular?api_key=explicit");

        List<String> keys = url.queryParameterValues(ApiKeyInterceptor.API_KEY_PARAM);
        assertEquals(1, keys.size());
        assertEquals("explicit", keys.get(0));
    }
}
