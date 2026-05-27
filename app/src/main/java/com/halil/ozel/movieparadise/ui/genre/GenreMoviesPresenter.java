package com.halil.ozel.movieparadise.ui.genre;

import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.ui.base.BaseRxPresenter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GenreMoviesPresenter extends BaseRxPresenter implements GenreMoviesContract.Presenter {

    private final TheMovieDbAPI theMovieDbAPI;
    private GenreMoviesContract.View view;
    private int page = 1;
    private int totalPages = 1;
    private boolean loading;

    public GenreMoviesPresenter(TheMovieDbAPI theMovieDbAPI) {
        this.theMovieDbAPI = theMovieDbAPI;
    }

    @Override
    public void attachView(GenreMoviesContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
        clear();
    }

    @Override
    public void loadMoviesByGenre(int genreId) {
        if (!canLoadMore()) {
            return;
        }

        loading = true;
        disposables.add(theMovieDbAPI.getMoviesByGenre(
                        genreId,
                        "popularity.desc",
                        false,
                        page,
                        Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loading = false;
                    if (response == null || response.getResults() == null) {
                        return;
                    }
                    totalPages = Math.max(1, response.getTotalPages());
                    page = response.getPage() + 1;
                    if (view != null) {
                        view.showMovies(response.getResults());
                    }
                }, throwable -> {
                    loading = false;
                    if (view != null) {
                        view.showLoadError(throwable);
                    }
                }));
    }

    @Override
    public boolean canLoadMore() {
        return !loading && page <= totalPages;
    }
}
