package com.halil.ozel.movieparadise.data.Api;

import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.CreditsResponse;
import com.halil.ozel.movieparadise.data.models.MovieDetails;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.data.models.TvCreditsResponse;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.data.models.VideoResponse;
import com.halil.ozel.movieparadise.data.models.Person;
import com.halil.ozel.movieparadise.data.models.PersonImagesResponse;
import com.halil.ozel.movieparadise.data.models.MovieCreditsResponse;
import com.halil.ozel.movieparadise.data.models.WatchProvidersResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.rxjava3.core.Observable;

public interface TheMovieDbAPI {

    @GET(HttpClientModule.NOW_PLAYING)
    Observable<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.TOP_RATED)
    Observable<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.UPCOMING)
    Observable<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.POPULAR)
    Observable<MovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.TV_ON_THE_AIR)
    Observable<TvShowResponse> getOnTheAir(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.TV_AIRING_TODAY)
    Observable<TvShowResponse> getAiringToday(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.TV_POPULAR)
    Observable<TvShowResponse> getPopularTv(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.TV_TOP_RATED)
    Observable<TvShowResponse> getTopRatedTv(@Query("api_key") String apiKey, @Query("page") int page);

    @GET(HttpClientModule.TV + "{id}")
    Observable<TvShow> getTvShowDetails(
            @Path("id") String tvShowId,
            @Query("api_key") String apiKey);

    @GET(HttpClientModule.TV + "{id}/credits")
    Observable<CreditsResponse> getTvCredits(@Path("id") String tvShowId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.TV + "{id}/recommendations")
    Observable<TvShowResponse> getTvRecommendations(@Path("id") String tvShowId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.TV + "{id}/similar")
    Observable<TvShowResponse> getSimilarTvShows(@Path("id") String tvShowId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.MOVIE + "{id}/recommendations")
    Observable<MovieResponse> getRecommendations(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.MOVIE + "{id}/similar")
    Observable<MovieResponse> getSimilarMovies(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.MOVIE + "{id}/credits")
    Observable<CreditsResponse> getCredits(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.MOVIE + "{id}")
    Observable<MovieDetails> getMovieDetails(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.MOVIE + "{id}/videos")
    Observable<VideoResponse> getMovieVideos(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.TV + "{id}/videos")
    Observable<VideoResponse> getTvVideos(@Path("id") String tvShowId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.DISCOVER_MOVIE)
    Observable<MovieResponse> discoverMovies(@Query("include_adult") boolean includeAdult,
                                             @Query("sort_by") String sortBy,
                                             @Query("page") int page,
                                             @Query("api_key") String apiKey);

    @GET(HttpClientModule.SEARCH_MOVIE)
    Observable<MovieResponse> getSearchMovies(@Query("query") String query, @Query("include_adult") Boolean include_adult, @Query("api_key") String apiKey);

    @GET(HttpClientModule.DISCOVER_MOVIE)
    Observable<MovieResponse> getMoviesByGenre(@Query("with_genres") int genreId,
                                               @Query("sort_by") String sortBy,
                                               @Query("include_adult") Boolean includeAdult,
                                               @Query("page") int page,
                                               @Query("api_key") String apiKey);

    @GET(HttpClientModule.PERSON + "{id}")
    Observable<Person> getPerson(@Path("id") String personId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.MOVIE + "{id}/watch/providers")
    Observable<WatchProvidersResponse> getMovieWatchProviders(@Path("id") String movieId,
                                                            @Query("api_key") String apiKey);

    @GET(HttpClientModule.TV + "{id}/watch/providers")
    Observable<WatchProvidersResponse> getTvWatchProviders(@Path("id") String tvShowId,
                                                         @Query("api_key") String apiKey);

    @GET(HttpClientModule.PERSON + "{id}/movie_credits")
    Observable<MovieCreditsResponse> getPersonMovieCredits(@Path("id") String personId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.PERSON + "{id}/tv_credits")
    Observable<TvCreditsResponse> getPersonTvCredits(@Path("id") String personId, @Query("api_key") String apiKey);

    @GET(HttpClientModule.PERSON + "{id}/images")
    Observable<PersonImagesResponse> getPersonImages(@Path("id") String personId, @Query("api_key") String apiKey);

}
