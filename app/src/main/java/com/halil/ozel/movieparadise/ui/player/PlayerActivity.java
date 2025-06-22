package com.halil.ozel.movieparadise.ui.player;

import android.app.Activity;
import android.os.Bundle;

import com.halil.ozel.movieparadise.R;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PlayerActivity extends Activity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        webView = findViewById(R.id.player);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String videoId = getIntent().getStringExtra("videoId");
        String html = "<html><body style=\"margin:0;\"><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" +
                videoId + "?autoplay=1\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe></body></html>";
        webView.loadData(html, "text/html", "utf-8");
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }
}
