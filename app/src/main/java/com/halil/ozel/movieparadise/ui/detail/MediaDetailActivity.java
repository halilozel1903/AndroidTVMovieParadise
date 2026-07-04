package com.halil.ozel.movieparadise.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.os.BundleCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.tv.TvDetailFragment;

public class MediaDetailActivity extends BaseTVActivity {

    private static final String TAG = "MediaDetailActivity";
    private static final String EXTRA_MEDIA_TYPE = "extra_media_type";
    private static final String MEDIA_TYPE_MOVIE = "movie";
    private static final String MEDIA_TYPE_TV = "tv";

    private GlideBackgroundManager glideBackgroundManager;
    private Movie movie;
    private TvShow tvShow;

    public static Intent newMovieIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MediaDetailActivity.class);
        intent.putExtra(EXTRA_MEDIA_TYPE, MEDIA_TYPE_MOVIE);
        intent.putExtra(Movie.class.getSimpleName(), movie);
        return intent;
    }

    public static Intent newTvIntent(Context context, TvShow tvShow) {
        Intent intent = new Intent(context, MediaDetailActivity.class);
        intent.putExtra(EXTRA_MEDIA_TYPE, MEDIA_TYPE_TV);
        intent.putExtra(TvShow.class.getSimpleName(), tvShow);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAfterTransition();
            }
        });

        Bundle extras = getIntent().getExtras();

        String mediaType = extras.getString(EXTRA_MEDIA_TYPE);
        if (MEDIA_TYPE_MOVIE.equals(mediaType) || extras.containsKey(Movie.class.getSimpleName())) {
            movie = BundleCompat.getParcelable(extras, Movie.class.getSimpleName(), Movie.class);
            addFragment(DetailFragment.newInstance(movie));
        } else if (MEDIA_TYPE_TV.equals(mediaType) || extras.containsKey(TvShow.class.getSimpleName())) {
            tvShow = BundleCompat.getParcelable(extras, TvShow.class.getSimpleName(), TvShow.class);
            addFragment(TvDetailFragment.newInstance(tvShow));
        } else {
            Log.e(TAG, "Unknown media type: " + mediaType);
            return;
        }

        glideBackgroundManager = new GlideBackgroundManager(this);
        updateBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing() || isDestroyed()) {
            return;
        }
        updateBackground();
    }

    @Override
    protected void onDestroy() {
        if (glideBackgroundManager != null) {
            glideBackgroundManager.release();
            glideBackgroundManager = null;
        }
        super.onDestroy();
    }

    private void updateBackground() {
        if (glideBackgroundManager == null) {
            return;
        }
        String backdropPath = movie != null ? movie.getBackdropPath()
                : tvShow != null ? tvShow.getBackdropPath() : null;
        if (backdropPath != null) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + backdropPath);
        } else {
            glideBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
