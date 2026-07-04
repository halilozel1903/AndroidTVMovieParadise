package com.halil.ozel.movieparadise.ui.detail;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.os.BundleCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;

public class PersonDetailActivity extends BaseTVActivity implements PersonBackgroundHost {

    private GlideBackgroundManager glideBackgroundManager;
    private CastMember castMember;

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
        if (extras != null) {
            castMember = BundleCompat.getParcelable(
                    extras, CastMember.class.getSimpleName(), CastMember.class);
        }

        addFragment(PersonDetailFragment.newInstance(castMember));
        glideBackgroundManager = new GlideBackgroundManager(this);
        showDefaultBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing() || isDestroyed()) {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        if (glideBackgroundManager != null) {
            glideBackgroundManager.release();
            glideBackgroundManager = null;
        }
        super.onDestroy();
    }

    @Override
    public void updatePersonBackground(@Nullable String backdropPath) {
        if (glideBackgroundManager == null || isFinishing() || isDestroyed()) {
            return;
        }
        if (backdropPath != null && !backdropPath.isEmpty()) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + backdropPath);
        } else {
            showDefaultBackground();
        }
    }

    private void showDefaultBackground() {
        if (glideBackgroundManager != null) {
            glideBackgroundManager.setBackground(
                    ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
