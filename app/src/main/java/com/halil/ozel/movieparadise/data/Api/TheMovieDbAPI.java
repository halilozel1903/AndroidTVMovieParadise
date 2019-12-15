package com.halil.ozel.movieparadise.data.Api;

import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.CreditsResponse;
import com.halil.ozel.movieparadise.data.models.MovieDetails;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.VideoResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TheMovieDbAPI {

    @GET(HttpClientModule.NOW_PLAYING)
    Observable<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.TOP_RATED)
    Observable<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.UPCOMING)
    Observable<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.POPULAR)
    Observable<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.MOVIE + "{id}/recommendations")
    Observable<MovieResponse> getRecommendations(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}/credits")
    Observable<CreditsResponse> getCredits(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}")
    Observable<MovieDetails> getMovieDetails(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}/videos")
    Observable<VideoResponse> getMovieVideos(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );


    @GET(HttpClientModule.SEARCH_MOVIE)
    Observable<MovieResponse> getSearchMovies(
            @Query("query") String query,
            @Query("api_key") String apiKey
    );

}
