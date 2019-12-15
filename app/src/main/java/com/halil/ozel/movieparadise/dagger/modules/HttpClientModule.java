package com.halil.ozel.movieparadise.dagger.modules;

import android.app.Application;

import com.halil.ozel.movieparadise.dagger.AppScope;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;

import java.io.File;
import java.util.concurrent.TimeUnit;


import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class HttpClientModule {

    private static final long DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    public static final String BACKDROP_URL = "https://image.tmdb.org/t/p/w1280/";
    public static final String POSTER_URL = "https://image.tmdb.org/t/p/w500/";
    public static final String API_URL = "https://api.themoviedb.org/3/";
    public static final String NOW_PLAYING = "movie/now_playing";
    public static final String POPULAR = "movie/popular";
    public static final String TOP_RATED = "movie/top_rated";
    public static final String UPCOMING = "movie/upcoming";
    public static final String MOVIE = "movie/";
    public static final String SEARCH_MOVIE = "search/movie/";

    @Provides
    @AppScope
    public OkHttpClient provideOkHttpClient(Application app) {
        File cacheDir = new File(app.getCacheDir(), "http");
        return new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .cache(new okhttp3.Cache(cacheDir, DISK_CACHE_SIZE))
                .build();
    }

    @Provides
    @AppScope
    public Retrofit provideFithubRestAdapter(MoshiConverterFactory moshiConverterFactory, OkHttpClient okHttpClient) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = okHttpClient.newBuilder()
                .addInterceptor(interceptor)
                .build();
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(moshiConverterFactory)
                .build();
    }

    @Provides
    public TheMovieDbAPI provideFithubApi(Retrofit restAdapter) {
        return restAdapter.create(TheMovieDbAPI.class);
    }

    @Provides
    @AppScope
    public MoshiConverterFactory provideMoshiConverterFactory() {
        return MoshiConverterFactory.create();
    }
}
