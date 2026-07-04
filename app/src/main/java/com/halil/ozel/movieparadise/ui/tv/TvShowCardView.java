package com.halil.ozel.movieparadise.ui.tv;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.base.BindableCardView;
import com.halil.ozel.movieparadise.ui.base.GlideUtils;

/** Card view used for TV show items. */
public class TvShowCardView extends BindableCardView<TvShow> {

    private final ImageView posterImageView;
    private final TextView titleTextView;

    public TvShowCardView(Context context) {
        super(context);
        posterImageView = findViewById(R.id.poster_iv);
        titleTextView = findViewById(R.id.title_tv);
    }

    @Override
    protected void bind(TvShow show) {
        String posterPath = show.getPosterPath();
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
        titleTextView.setText(show.getName());
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
