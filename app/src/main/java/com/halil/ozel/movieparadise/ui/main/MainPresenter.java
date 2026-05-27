package com.halil.ozel.movieparadise.ui.main;

import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.ui.base.BaseRxPresenter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainPresenter extends BaseRxPresenter implements MainContract.Presenter {

    private final TheMovieDbAPI theMovieDbAPI;
    private MainContract.View view;

    public MainPresenter(TheMovieDbAPI theMovieDbAPI) {
        this.theMovieDbAPI = theMovieDbAPI;
    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
        clear();
    }

    @Override
    public void loadHomeSections(MainContract.RowPageProvider rowPageProvider) {
        loadMovieSection(
                MainContract.NOW_PLAYING,
                "now playing",
                theMovieDbAPI.getNowPlayingMovies(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.NOW_PLAYING)));
        loadMovieSection(
                MainContract.TOP_RATED,
                "top rated",
                theMovieDbAPI.getTopRatedMovies(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.TOP_RATED)));
        loadMovieSection(
                MainContract.POPULAR,
                "popular",
                theMovieDbAPI.getPopularMovies(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.POPULAR)));
        loadMovieSection(
                MainContract.UPCOMING,
                "upcoming",
                theMovieDbAPI.getUpcomingMovies(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.UPCOMING)));

        loadTvSection(
                MainContract.TV_ON_THE_AIR,
                "on the air",
                theMovieDbAPI.getOnTheAir(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.TV_ON_THE_AIR)));
        loadTvSection(
                MainContract.TV_AIRING_TODAY,
                "airing today",
                theMovieDbAPI.getAiringToday(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.TV_AIRING_TODAY)));
        loadTvSection(
                MainContract.TV_POPULAR,
                "popular tv",
                theMovieDbAPI.getPopularTv(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.TV_POPULAR)));
        loadTvSection(
                MainContract.TV_TOP_RATED,
                "top rated tv",
                theMovieDbAPI.getTopRatedTv(Config.API_KEY_URL, rowPageProvider.getPage(MainContract.TV_TOP_RATED)));
    }

    private void loadMovieSection(int rowId, String source, Observable<MovieResponse> request) {
        disposables.add(request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (view != null && response != null && response.getResults() != null) {
                        view.showMovieResults(rowId, response.getResults());
                    }
                }, throwable -> {
                    if (view != null) {
                        view.showLoadError(source, throwable);
                    }
                }));
    }

    private void loadTvSection(int rowId, String source, Observable<TvShowResponse> request) {
        disposables.add(request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (view != null && response != null && response.getResults() != null) {
                        view.showTvResults(rowId, response.getResults());
                    }
                }, throwable -> {
                    if (view != null) {
                        view.showLoadError(source, throwable);
                    }
                }));
    }
}
