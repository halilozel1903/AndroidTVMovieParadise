package com.halil.ozel.movieparadise.ui.genre;

import com.halil.ozel.movieparadise.data.models.Movie;

import java.util.List;

public interface GenreMoviesContract {

    interface View {
        void onLoadStarted(boolean isFirstPage);
        void showMovies(List<Movie> movies);
        void showEmpty();
        void showLoadError(Throwable throwable);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadMoviesByGenre(int genreId);
        void retry(int genreId);
        boolean canLoadMore();
    }
}
