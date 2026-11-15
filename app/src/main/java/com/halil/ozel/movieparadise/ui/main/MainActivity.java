package com.halil.ozel.movieparadise.ui.main;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;

public class MainActivity extends BaseTVActivity {

    private static final long EXIT_CONFIRM_WINDOW_MS = 2_000L;

    private long lastBackPressedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        addFragment(MainFragment.newInstance());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });
    }

    /**
     * Back first returns to the row headers; from there a second press within
     * {@link #EXIT_CONFIRM_WINDOW_MS} leaves the app, so a stray press never closes it.
     */
    private void handleBack() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.tv_frame_content);
        if (fragment instanceof MainFragment && ((MainFragment) fragment).showHeadersIfHidden()) {
            return;
        }
        long now = SystemClock.elapsedRealtime();
        if (lastBackPressedAt != 0 && now - lastBackPressedAt < EXIT_CONFIRM_WINDOW_MS) {
            finish();
            return;
        }
        lastBackPressedAt = now;
        Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
    }
}
