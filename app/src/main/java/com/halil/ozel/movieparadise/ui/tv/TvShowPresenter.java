package com.halil.ozel.movieparadise.ui.tv;

import androidx.leanback.widget.Presenter;
import android.view.ViewGroup;
import com.halil.ozel.movieparadise.data.models.TvShow;

/** Presenter used to render TV show cards. */
public class TvShowPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(new TvShowCardView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((TvShowCardView) viewHolder.view).bind((TvShow) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        // no-op
    }
}
