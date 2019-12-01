package com.halil.ozel.movieparadise.ui.details;

import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.MovieDetails;


public class MovieDetailsDescriptionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_details, parent, false);
        return new MovieDetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        MovieDetails movie = (MovieDetails) item;
        MovieDetailsViewHolder holder = (MovieDetailsViewHolder) viewHolder;
        holder.bind(movie);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
