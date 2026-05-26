package com.halil.ozel.movieparadise.ui.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.ui.main.MainActivity;

/**
 * Base activity for Leanback TV screens.
 * Hosts a single fragment inside R.id.tv_frame_content.
 */
public class BaseTVActivity extends FragmentActivity {

    private boolean routeBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    /**
     * Replaces the content frame with the given fragment.
     * Guards against duplicate addition on configuration change.
     */
    public void addFragment(Fragment fragment) {
        if (getSupportFragmentManager().findFragmentById(R.id.tv_frame_content) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.tv_frame_content, fragment);
            transaction.commitNow();
        }
    }

    protected void finishOrReturnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    protected void registerMainBackFallback() {
        routeBackToMain = true;
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishOrReturnToMain();
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (routeBackToMain
                && keyCode == KeyEvent.KEYCODE_BACK) {
            finishOrReturnToMain();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @SuppressLint({"MissingSuperCall", "GestureBackNavigation"})
    @Override
    public void onBackPressed() {
        if (routeBackToMain) {
            finishOrReturnToMain();
        } else {
            super.onBackPressed();
        }
    }
}
