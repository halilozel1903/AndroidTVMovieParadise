package com.halil.ozel.movieparadise.ui.tv;

import android.os.Bundle;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;

/** Activity that hosts {@link TvMainFragment}. */
public class TvMainActivity extends BaseTVActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(TvMainFragment.newInstance());
    }
}
