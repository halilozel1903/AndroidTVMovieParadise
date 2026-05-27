package com.halil.ozel.movieparadise.ui.detail;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;


public class DetailDescriptionPresenter extends Presenter {

    public interface OnGenreClickListener {
        void onGenreClicked(Genre genre);
    }

    private final OnGenreClickListener genreClickListener;

    public DetailDescriptionPresenter(OnGenreClickListener genreClickListener) {
        this.genreClickListener = genreClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_detail, parent, false);
        return new DetailViewHolder(view, genreClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, Object item) {
        DetailViewHolder holder = (DetailViewHolder) viewHolder;
        holder.bind(item);
    }

    @Override
    public void onUnbindViewHolder(@NonNull ViewHolder viewHolder) {}
}
