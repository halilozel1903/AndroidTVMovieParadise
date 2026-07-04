package com.halil.ozel.movieparadise.ui.search;

import com.halil.ozel.movieparadise.data.models.Movie;

import java.util.List;

public interface SearchContract {

    interface View {
        void onSearchStarted();
        void clearResults();
        void showResults(List<Movie> movies);
        void showEmpty();
        void showSearchError(Throwable throwable);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void search(String query);
        void retryLastSearch();
    }
}
