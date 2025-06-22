package com.halil.ozel.movieparadise.ui.detail;

import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;

public class PersonDetailActivity extends BaseTVActivity {

    private GlideBackgroundManager glideBackgroundManager;
    private CastMember castMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        castMember = getIntent().getParcelableExtra(CastMember.class.getSimpleName());
        addFragment(PersonDetailFragment.newInstance(castMember));
        glideBackgroundManager = new GlideBackgroundManager(this);
        updateBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackground();
    }

    private void updateBackground() {
        if (castMember != null && castMember.getProfilePath() != null) {
            glideBackgroundManager.loadImage(HttpClientModule.POSTER_URL + castMember.getProfilePath());
        } else {
            glideBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
