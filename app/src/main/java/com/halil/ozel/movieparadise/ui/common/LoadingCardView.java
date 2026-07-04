package com.halil.ozel.movieparadise.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.halil.ozel.movieparadise.R;

public class LoadingCardView extends FrameLayout {

    private final View skeletonView;
    private Animation pulseAnimation;

    public LoadingCardView(Context context) {
        super(context);
        setFocusable(false);
        LayoutInflater.from(context).inflate(R.layout.card_loading, this, true);
        skeletonView = findViewById(R.id.skeleton_view);
    }

    public void startAnimation() {
        if (pulseAnimation == null) {
            pulseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.skeleton_pulse);
        }
        skeletonView.startAnimation(pulseAnimation);
    }

    public void stopAnimation() {
        skeletonView.clearAnimation();
    }
}
