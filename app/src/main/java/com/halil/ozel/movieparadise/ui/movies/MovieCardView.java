package com.halil.ozel.movieparadise.ui.movies;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.base.BindableCardView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */
public class MovieCardView extends BindableCardView<Movie> {

    @BindView(R.id.poster_iv)
    ImageView mPosterIV;

    @BindView(R.id.vote_average_tv)
    TextView mVoteAverageTV;

    public MovieCardView(Context context) {
        super(context);
        ButterKnife.bind(this);
    }

    @Override
    protected void bind(Movie data) {
        Glide.with(getContext())
                .load(HttpClientModule.POSTER_URL + data.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPosterIV);
        mVoteAverageTV.setText(String.format(Locale.getDefault(), "%.2f", data.getVoteAverage()));
    }

    public ImageView getPosterIV() {
        return mPosterIV;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.card_movie;
    }
}