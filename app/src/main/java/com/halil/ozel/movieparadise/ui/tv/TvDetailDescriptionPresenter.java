package com.halil.ozel.movieparadise.ui.tv;

import androidx.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.TvShow;

/** Presenter responsible for binding TV show details. */
public class TvDetailDescriptionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_detail, parent, false);
        return new TvDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        TvShow tvShow = (TvShow) item;
        TvDetailViewHolder holder = (TvDetailViewHolder) viewHolder;
        holder.bind(tvShow);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {}
}
