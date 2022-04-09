package com.halil.ozel.movieparadise.ui.base;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import androidx.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class GlideBackgroundManager {

    private static final int BACKGROUND_UPDATE_DELAY = 200;

    private WeakReference<Activity> mActivityWeakReference;
    private BackgroundManager mBackgroundManager;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private String mBackgroundURI;
    private Timer mBackgroundTimer;

    public static GlideBackgroundManager instance;


    //  The activity to which this WindowManager is attached
    public GlideBackgroundManager(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
        mBackgroundManager = BackgroundManager.getInstance(activity);
        mBackgroundManager.attach(activity.getWindow());
    }

    private SimpleTarget<GlideDrawable> mGlideDrawableSimpleTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            setBackground(resource);
        }
    };

    public void loadImage(String imageUrl) {
        mBackgroundURI = imageUrl;
        startBackgroundTimer();
    }

    public void setBackground(Drawable drawable) {
        if (mBackgroundManager != null) {
            if (!mBackgroundManager.isAttached()) {
                mBackgroundManager.attach(mActivityWeakReference.get().getWindow());
            }
            mBackgroundManager.setDrawable(drawable);
        }
    }

    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(() -> {
                if (mBackgroundURI != null) {
                    updateBackground();
                }
            });
        }
    }


    // Cancels an ongoing background change
    public void cancelBackgroundChange() {
        mBackgroundURI = null;
        cancelTimer();
    }



    // Stops the timer
    private void cancelTimer() {
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
    }



    // Starts the background change timer
    private void startBackgroundTimer() {
        cancelTimer();
        mBackgroundTimer = new Timer();
        /* set delay time to reduce too much background image loading process */
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }


    // Updates the background with the last known URI
    public void updateBackground() {
        if (mActivityWeakReference.get() != null) {
            Glide.with(mActivityWeakReference.get())
                    .load(mBackgroundURI)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mGlideDrawableSimpleTarget);
        }
    }

}
