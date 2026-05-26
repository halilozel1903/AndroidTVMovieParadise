package com.halil.ozel.movieparadise.ui.base;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;

/**
 * Manages the background image for Leanback activities using Glide.
 * Uses Handler.postDelayed instead of legacy Timer/TimerTask.
 */
public class GlideBackgroundManager {

    private static final String TAG = "GlideBackgroundMgr";
    private static final int BACKGROUND_UPDATE_DELAY_MS = 200;

    private final WeakReference<Activity> mActivityRef;
    private final BackgroundManager mBackgroundManager;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private String mPendingBackgroundUrl;
    private Runnable mUpdateRunnable;

    private final CustomTarget<Drawable> mTarget = new CustomTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            setBackground(resource);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            // no-op
        }
    };

    public GlideBackgroundManager(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        mBackgroundManager = BackgroundManager.getInstance(activity);
        mBackgroundManager.attach(activity.getWindow());
    }

    /**
     * Schedules a background image load with a short debounce delay,
     * so rapid selection changes don't flood network requests.
     */
    public void loadImage(String imageUrl) {
        mPendingBackgroundUrl = imageUrl;
        cancelPending();
        mUpdateRunnable = this::updateBackground;
        mHandler.postDelayed(mUpdateRunnable, BACKGROUND_UPDATE_DELAY_MS);
    }

    public void setBackground(Drawable drawable) {
        if (mBackgroundManager != null && mBackgroundManager.isAttached()) {
            mBackgroundManager.setDrawable(drawable);
        }
    }

    /** Cancels any pending background update. */
    public void cancelBackgroundChange() {
        mPendingBackgroundUrl = null;
        cancelPending();
    }

    private void cancelPending() {
        if (mUpdateRunnable != null) {
            mHandler.removeCallbacks(mUpdateRunnable);
            mUpdateRunnable = null;
        }
    }

    private void updateBackground() {
        Activity activity = mActivityRef.get();
        if (activity == null || activity.isDestroyed()) {
            Log.w(TAG, "Activity is gone, skipping background update");
            return;
        }
        if (mPendingBackgroundUrl == null) return;

        Glide.with(activity)
                .load(mPendingBackgroundUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mTarget);
    }
}
