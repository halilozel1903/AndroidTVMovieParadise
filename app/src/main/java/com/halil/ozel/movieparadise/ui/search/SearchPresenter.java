package com.halil.ozel.movieparadise.ui.search;

import androidx.annotation.Nullable;

import com.halil.ozel.movieparadise.data.api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.ui.base.BaseRxPresenter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenter extends BaseRxPresenter implements SearchContract.Presenter {

    private static final long SEARCH_DEBOUNCE_MS = 350L;

    private final TheMovieDbAPI theMovieDbAPI;
    @Nullable
    private SearchContract.View view;
    @Nullable
    private Disposable searchDisposable;
    private String lastQuery = "";

    @Inject
    public SearchPresenter(TheMovieDbAPI theMovieDbAPI) {
        this.theMovieDbAPI = theMovieDbAPI;
    }

    @Override
    public void attachView(SearchContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
        cancelActiveSearch();
        clear();
    }

    @Override
    public void search(String query) {
        String normalizedQuery = query == null ? "" : query.trim();
        if (normalizedQuery.equals(lastQuery)) {
            return;
        }

        lastQuery = normalizedQuery;
        cancelActiveSearch();
        if (view != null) {
            view.clearResults();
        }
        if (normalizedQuery.isEmpty()) {
            return;
        }

        executeSearch(normalizedQuery, SEARCH_DEBOUNCE_MS);
    }

    @Override
    public void retryLastSearch() {
        if (lastQuery.isEmpty()) {
            return;
        }
        cancelActiveSearch();
        if (view != null) {
            view.clearResults();
        }
        executeSearch(lastQuery, 0);
    }

    private void executeSearch(String normalizedQuery, long delayMs) {
        if (view != null) {
            view.onSearchStarted();
        }

        // Typing with a remote is slow and noisy: wait for a pause before hitting the network.
        // A newer query disposes this chain, which also cancels the pending timer.
        searchDisposable = Observable.timer(delayMs, TimeUnit.MILLISECONDS, Schedulers.io())
                .flatMap(tick -> theMovieDbAPI.getSearchMovies(normalizedQuery, false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (view == null || !normalizedQuery.equals(lastQuery)) {
                        return;
                    }
                    if (response.getResults() == null || response.getResults().isEmpty()) {
                        view.showEmpty();
                    } else {
                        view.showResults(response.getResults());
                    }
                }, throwable -> {
                    if (view != null) {
                        view.showSearchError(throwable);
                    }
                });
        disposables.add(searchDisposable);
    }

    private void cancelActiveSearch() {
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }
        searchDisposable = null;
    }
}
