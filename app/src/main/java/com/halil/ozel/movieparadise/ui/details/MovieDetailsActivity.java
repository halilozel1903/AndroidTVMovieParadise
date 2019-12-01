package com.halil.ozel.movieparadise.ui.details;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.base.BaseTvActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;


public class MovieDetailsActivity extends BaseTvActivity {

    GlideBackgroundManager mBackgroundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the movie through the intent
        Movie movie = getIntent().getExtras().getParcelable(Movie.class.getSimpleName());
        MovieDetailsFragment detailsFragment = MovieDetailsFragment.newInstance(movie);
        addFragment(detailsFragment); // Method from BaseTvActivity

        // Sets the background of the activity to the backdrop of the movie
        mBackgroundManager = new GlideBackgroundManager(this);
        if (movie != null && movie.getBackdropPath() != null) {
            mBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + movie.getBackdropPath());
        } else {
            mBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
