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

/** Card view used for TV show items. */
public class TvShowCardView extends BindableCardView<TvShow> {

    ImageView mPosterIV;
    TextView title_tv;

    public TvShowCardView(Context context) {
        super(context);
        mPosterIV = findViewById(R.id.poster_iv);
        title_tv = findViewById(R.id.title_tv);
    }

    @Override
    protected void bind(TvShow show) {
        String posterPath = show.getPosterPath();
        if (posterPath == null || posterPath.isEmpty()) {
            Glide.with(getContext())
                    .load(R.drawable.popcorn)
                    .into(mPosterIV);
        } else {
            Glide.with(getContext())
                    .load(HttpClientModule.POSTER_URL + posterPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.popcorn)
                    .into(mPosterIV);
        }
        title_tv.setText(show.getName());
    }

    public ImageView getPosterIV() {
        return mPosterIV;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.card_movie;
    }
}
