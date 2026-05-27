package com.halil.ozel.movieparadise.ui.tv;

import com.halil.ozel.movieparadise.data.models.TvShow;

import java.util.List;

public interface TvMainContract {

    int ON_THE_AIR = 0;
    int AIRING_TODAY = 1;
    int POPULAR = 2;
    int TOP_RATED = 3;

    interface View {
        void showTvResults(int rowId, List<TvShow> shows);
        void showLoadError(String source, Throwable throwable);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadSections(RowPageProvider rowPageProvider);
    }

    interface RowPageProvider {
        int getPage(int rowId);
    }
}
