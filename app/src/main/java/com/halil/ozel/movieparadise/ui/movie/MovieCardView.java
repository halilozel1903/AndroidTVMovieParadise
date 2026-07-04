package com.halil.ozel.movieparadise.ui.movie;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.base.BindableCardView;
import com.halil.ozel.movieparadise.ui.base.GlideUtils;

public class MovieCardView extends BindableCardView<Movie> {

    private final ImageView posterImageView;
    private final TextView titleTextView;

    public MovieCardView(Context context) {
        super(context);
        posterImageView = findViewById(R.id.poster_iv);
        titleTextView = findViewById(R.id.title_tv);
    }

    @Override
    protected void bind(Movie movie) {
        String posterPath = movie.getPosterPath();
        if (posterPath == null || posterPath.isEmpty()) {
            Glide.with(getContext())
                    .load(R.drawable.popcorn)
                    .into(posterImageView);
        } else {
            Glide.with(getContext())
                    .load(HttpClientModule.POSTER_URL + posterPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.popcorn)
                    .error(R.drawable.popcorn)
                    .into(posterImageView);
        }
        titleTextView.setText(movie.getTitle());
    }

    void unbind() {
        GlideUtils.clearImageView(posterImageView);
        titleTextView.setText(null);
    }

    public ImageView getPosterIV() {
        return posterImageView;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.card_movie;
    }
}
