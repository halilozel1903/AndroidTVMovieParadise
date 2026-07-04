package com.halil.ozel.movieparadise.ui.search;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;

/**
 * Hosts the {@link SearchFragment} with TV styling and dynamic background.
 */
public class SearchActivity extends BaseTVActivity {

    private GlideBackgroundManager glideBackgroundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glideBackgroundManager = new GlideBackgroundManager(this);
        glideBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        if (getSupportFragmentManager().findFragmentById(R.id.tv_frame_content) == null) {
            addFragment(SearchFragment.newInstance());
        }
    }

    public void updateBackground(String backdropPath) {
        if (glideBackgroundManager == null) {
            return;
        }
        if (backdropPath != null) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + backdropPath);
        } else {
            glideBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
