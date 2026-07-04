package com.halil.ozel.movieparadise.ui.main;

import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.ui.base.BaseRxPresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainPresenter extends BaseRxPresenter implements MainContract.Presenter {

    private final TheMovieDbAPI theMovieDbAPI;
    private MainContract.View view;

    @Inject
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
    public void loadHomeSection(int rowId, int page) {
        switch (rowId) {
            case MainContract.NOW_PLAYING:
                loadMovieSection(rowId, "now playing",
                        theMovieDbAPI.getNowPlayingMovies(Config.API_KEY_URL, page));
                break;
            case MainContract.TOP_RATED:
                loadMovieSection(rowId, "top rated",
                        theMovieDbAPI.getTopRatedMovies(Config.API_KEY_URL, page));
                break;
            case MainContract.POPULAR:
                loadMovieSection(rowId, "popular",
                        theMovieDbAPI.getPopularMovies(Config.API_KEY_URL, page));
                break;
            case MainContract.UPCOMING:
                loadMovieSection(rowId, "upcoming",
                        theMovieDbAPI.getUpcomingMovies(Config.API_KEY_URL, page));
                break;
            case MainContract.TV_ON_THE_AIR:
                loadTvSection(rowId, "on the air",
                        theMovieDbAPI.getOnTheAir(Config.API_KEY_URL, page));
                break;
            case MainContract.TV_AIRING_TODAY:
                loadTvSection(rowId, "airing today",
                        theMovieDbAPI.getAiringToday(Config.API_KEY_URL, page));
                break;
            case MainContract.TV_POPULAR:
                loadTvSection(rowId, "popular tv",
                        theMovieDbAPI.getPopularTv(Config.API_KEY_URL, page));
                break;
            case MainContract.TV_TOP_RATED:
                loadTvSection(rowId, "top rated tv",
                        theMovieDbAPI.getTopRatedTv(Config.API_KEY_URL, page));
                break;
            default:
                break;
        }
    }

    private void loadMovieSection(int rowId, String source, Observable<MovieResponse> request) {
        disposables.add(request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (view != null) {
                        view.showMovieResponse(rowId, response);
                    }
                }, throwable -> {
                    if (view != null) {
                        view.showLoadError(rowId, source, throwable);
                    }
                }));
    }

    private void loadTvSection(int rowId, String source, Observable<TvShowResponse> request) {
        disposables.add(request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (view != null) {
                        view.showTvResponse(rowId, response);
                    }
                }, throwable -> {
                    if (view != null) {
                        view.showLoadError(rowId, source, throwable);
                    }
                }));
    }
}
