package com.halil.ozel.movieparadise.ui.detail;

import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;


public class DetailActivity extends BaseTVActivity {

    private GlideBackgroundManager glideBackgroundManager;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movie = getIntent().getExtras().getParcelable(Movie.class.getSimpleName());
        DetailFragment detailsFragment = DetailFragment.newInstance(movie);
        addFragment(detailsFragment);

        glideBackgroundManager = new GlideBackgroundManager(this);
        updateBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackground();
    }

    private void updateBackground() {
        if (movie != null && movie.getBackdropPath() != null) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + movie.getBackdropPath());
        } else {
            glideBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
