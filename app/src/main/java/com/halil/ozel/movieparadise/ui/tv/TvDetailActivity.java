package com.halil.ozel.movieparadise.ui.tv;

import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;

/** Activity displaying details for a TV show. */
public class TvDetailActivity extends BaseTVActivity {

    private GlideBackgroundManager glideBackgroundManager;
    private TvShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvShow = getIntent().getExtras().getParcelable(TvShow.class.getSimpleName());
        TvDetailFragment detailsFragment = TvDetailFragment.newInstance(tvShow);
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
        if (tvShow != null && tvShow.getBackdropPath() != null) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + tvShow.getBackdropPath());
        } else {
            glideBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
