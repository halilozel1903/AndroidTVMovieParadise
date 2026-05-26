package com.halil.ozel.movieparadise.ui.player;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.halil.ozel.movieparadise.R;

/**
 * Fullscreen player activity that embeds a YouTube video via WebView.
 * Extends AppCompatActivity and uses modern immersive mode API.
 */
public class PlayerActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_player);

        // Hide system bars for immersive fullscreen
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        webView = findViewById(R.id.player);
        configureWebView();

        String videoId = getIntent().getStringExtra("videoId");
        if (videoId != null && !videoId.isEmpty()) {
            loadVideo(videoId);
        }
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());
    }

    private void loadVideo(String videoId) {
        String html = "<!DOCTYPE html><html><body style=\"margin:0;padding:0;background:#000;\">"
                + "<iframe width=\"100%\" height=\"100%\""
                + " src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1\""
                + " frameborder=\"0\""
                + " allow=\"autoplay; encrypted-media; fullscreen\""
                + " allowfullscreen></iframe>"
                + "</body></html>";

        // loadDataWithBaseURL avoids encoding issues with loadData
        webView.loadDataWithBaseURL(
                "https://www.youtube.com", html, "text/html", "UTF-8", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
