package com.halil.ozel.movieparadise.ui.main;

import android.os.Bundle;
import android.view.KeyEvent;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // TV ana ekranda yanlışlıkla back ile uygulamayı kapatma.
    }
}
