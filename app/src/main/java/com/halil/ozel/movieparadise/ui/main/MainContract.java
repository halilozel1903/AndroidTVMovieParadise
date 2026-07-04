package com.halil.ozel.movieparadise.ui.main;

import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;

public interface MainContract {

    int NOW_PLAYING = 0;
    int TOP_RATED = 1;
    int POPULAR = 2;
    int UPCOMING = 3;
    int TV_ON_THE_AIR = 4;
    int TV_AIRING_TODAY = 5;
    int TV_POPULAR = 6;
    int TV_TOP_RATED = 7;

    interface View {
        void showMovieResponse(int rowId, MovieResponse response);
        void showTvResponse(int rowId, TvShowResponse response);
        void showLoadError(int rowId, String source, Throwable throwable);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadHomeSection(int rowId, int page);
    }
}
