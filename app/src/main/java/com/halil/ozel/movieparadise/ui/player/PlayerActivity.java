package com.halil.ozel.movieparadise.ui.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.ui.detail.TrailerHelper;

/**
 * Fullscreen trailer player — WebView embed, YouTube uygulamasına fallback.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final String YOUTUBE_TV_PACKAGE = "com.google.android.youtube.tv";
    private static final String YOUTUBE_MOBILE_PACKAGE = "com.google.android.youtube";

    private WebView webView;
    private View loadingOverlay;
    private View errorOverlay;
    private TextView retryButton;
    private TextView openYoutubeButton;
    private String videoId;
    private boolean webViewFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_player);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        webView = findViewById(R.id.player);
        loadingOverlay = findViewById(R.id.loading_overlay);
        errorOverlay = findViewById(R.id.error_overlay);
        retryButton = findViewById(R.id.retry_button);
        openYoutubeButton = findViewById(R.id.open_youtube_button);
        TextView backButton = findViewById(R.id.back_button);

        configureWebView();
        retryButton.setOnClickListener(v -> playInWebView());
        openYoutubeButton.setOnClickListener(v -> openInYouTubeApp());
        backButton.setOnClickListener(v -> finish());

        videoId = getIntent().getStringExtra(TrailerHelper.EXTRA_VIDEO_ID);
        if (videoId == null || videoId.isEmpty()) {
            videoId = getIntent().getStringExtra("videoId");
        }
        if (videoId != null && !videoId.isEmpty()) {
            playInWebView();
        } else {
            showError();
        }
    }

    private void playInWebView() {
        if (videoId == null || videoId.isEmpty()) {
            showError();
            return;
        }
        webViewFailed = false;
        showLoading();
        String embedUrl = "https://www.youtube.com/embed/" + videoId
                + "?autoplay=1&controls=1&fs=1&rel=0&playsinline=1&modestbranding=1";
        webView.loadUrl(embedUrl);
    }

    private void configureWebView() {
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webViewFailed) {
                    hideLoading();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                if (request.isForMainFrame()) {
                    webViewFailed = true;
                    showError();
                }
            }
        });
    }

    private void openInYouTubeApp() {
        if (videoId == null || videoId.isEmpty()) {
            return;
        }
        Uri watchUri = Uri.parse("https://www.youtube.com/watch?v=" + videoId);

        Intent tvIntent = new Intent(Intent.ACTION_VIEW, watchUri);
        tvIntent.setPackage(YOUTUBE_TV_PACKAGE);
        if (tvIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(tvIntent);
            return;
        }

        Intent mobileIntent = new Intent(Intent.ACTION_VIEW, watchUri);
        mobileIntent.setPackage(YOUTUBE_MOBILE_PACKAGE);
        if (mobileIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mobileIntent);
            return;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, watchUri);
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        }
    }

    private void showLoading() {
        errorOverlay.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);
        webView.setVisibility(View.INVISIBLE);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        loadingOverlay.setVisibility(View.GONE);
        webView.setVisibility(View.INVISIBLE);
        errorOverlay.setVisibility(View.VISIBLE);
        retryButton.post(retryButton::requestFocus);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
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
