package com.halil.ozel.movieparadise.ui.main;

import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.TvShow;

import java.util.List;

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
        void showMovieResults(int rowId, List<Movie> movies);
        void showTvResults(int rowId, List<TvShow> shows);
        void showLoadError(int rowId, String source, Throwable throwable);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadHomeSections(RowPageProvider rowPageProvider);
        void loadHomeSection(int rowId, int page);
    }

    interface RowPageProvider {
        int getPage(int rowId);
    }
}
