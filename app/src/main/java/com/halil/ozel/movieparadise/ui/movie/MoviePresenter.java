package com.halil.ozel.movieparadise.ui.movie;

import androidx.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.halil.ozel.movieparadise.data.models.Movie;


public class MoviePresenter extends Presenter {

    public MoviePresenter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(new MovieCardView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((MovieCardView) viewHolder.view).bind((Movie) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
