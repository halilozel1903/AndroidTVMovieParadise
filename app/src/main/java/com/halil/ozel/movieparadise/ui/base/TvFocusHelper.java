package com.halil.ozel.movieparadise.ui.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.halil.ozel.movieparadise.R;

/**
 * Leanback dışı odaklanabilir öğeler (chip vb.) için sade selector arka plan.
 * Kartlarda Leanback {@link TvRows} focus highlight kullanılır.
 */
public final class TvFocusHelper {

    private TvFocusHelper() {
    }

    public static void prepareFocusable(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setDefaultFocusHighlightEnabled(false);
    }

    public static void applyChip(TextView view) {
        prepareFocusable(view);
        view.setBackgroundResource(R.drawable.tv_focus_chip);
    }

    public static void applyIconChip(View view) {
        prepareFocusable(view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            group.setClipChildren(false);
            group.setClipToPadding(false);
        }
        view.setBackgroundResource(R.drawable.tv_focus_icon);
    }

    public static void styleActionLabel(TextView label) {
        label.setFocusable(false);
        label.setClickable(false);
        label.setBackgroundResource(R.drawable.tv_focus_button_idle);
    }
}
