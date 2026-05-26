package com.halil.ozel.movieparadise.ui.main;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;

import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;


public class MainActivity extends BaseTVActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(MainFragment.newInstance());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // TV ana ekranda yanlışlıkla back ile uygulamayı kapatma.
            }
        });
    }
}
