package com.halil.ozel.movieparadise.ui.base;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public final class GlideUtils {

    private GlideUtils() {
    }

    public static void clearImageView(ImageView imageView) {
        if (imageView == null) {
            return;
        }
        imageView.setImageDrawable(null);
        Context context = imageView.getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return;
            }
        }
        Glide.with(context).clear(imageView);
    }
}
