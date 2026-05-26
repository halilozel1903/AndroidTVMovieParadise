package com.halil.ozel.movieparadise.dagger.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.halil.ozel.movieparadise.dagger.AppScope;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Dagger module that provides network-related dependencies.
 * Uses GsonConverterFactory (replaces the previous MoshiConverterFactory).
 */
@Module
public class HttpClientModule {

    private static final long DISK_CACHE_SIZE = 50 * 1024 * 1024L; // 50 MB

    public static final String BACKDROP_URL    = "https://image.tmdb.org/t/p/w1280/";
    public static final String POSTER_URL      = "https://image.tmdb.org/t/p/w500/";
    public static final String API_URL         = "https://api.themoviedb.org/3/";
    public static final String NOW_PLAYING     = "movie/now_playing";
    public static final String POPULAR         = "movie/popular";
    public static final String TOP_RATED       = "movie/top_rated";
    public static final String UPCOMING        = "movie/upcoming";
    public static final String MOVIE           = "movie/";
    public static final String DISCOVER_MOVIE  = "discover/movie";
    public static final String TV_TOP_RATED    = "tv/top_rated";
    public static final String TV_POPULAR      = "tv/popular";
    public static final String TV_ON_THE_AIR   = "tv/on_the_air";
    public static final String TV_AIRING_TODAY = "tv/airing_today";
    public static final String PERSON          = "person/";
    public static final String SEARCH_MOVIE    = "search/movie";

    @Provides
    @AppScope
    public Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    @Provides
    @AppScope
    public OkHttpClient provideOkHttpClient(Application app) {
        File cacheDir = new File(app.getCacheDir(), "http_cache");
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cache(new Cache(cacheDir, DISK_CACHE_SIZE))
                .addInterceptor(logging)
                .build();
    }

    @Provides
    @AppScope
    public Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @AppScope
    public TheMovieDbAPI provideMovieDbApi(Retrofit retrofit) {
        return retrofit.create(TheMovieDbAPI.class);
    }
}
