package com.halil.ozel.movieparadise.ui.tv;

import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.ui.base.BaseRxPresenter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TvMainPresenter extends BaseRxPresenter implements TvMainContract.Presenter {

    private final TheMovieDbAPI theMovieDbAPI;
    private TvMainContract.View view;

    public TvMainPresenter(TheMovieDbAPI theMovieDbAPI) {
        this.theMovieDbAPI = theMovieDbAPI;
    }

    @Override
    public void attachView(TvMainContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
        clear();
    }

    @Override
    public void loadSections(TvMainContract.RowPageProvider rowPageProvider) {
        loadSection(
                TvMainContract.ON_THE_AIR,
                "on the air",
                theMovieDbAPI.getOnTheAir(Config.API_KEY_URL, rowPageProvider.getPage(TvMainContract.ON_THE_AIR)));
        loadSection(
                TvMainContract.AIRING_TODAY,
                "airing today",
                theMovieDbAPI.getAiringToday(Config.API_KEY_URL, rowPageProvider.getPage(TvMainContract.AIRING_TODAY)));
        loadSection(
                TvMainContract.POPULAR,
                "popular",
                theMovieDbAPI.getPopularTv(Config.API_KEY_URL, rowPageProvider.getPage(TvMainContract.POPULAR)));
        loadSection(
                TvMainContract.TOP_RATED,
                "top rated",
                theMovieDbAPI.getTopRatedTv(Config.API_KEY_URL, rowPageProvider.getPage(TvMainContract.TOP_RATED)));
    }

    private void loadSection(int rowId, String source, Observable<TvShowResponse> request) {
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
