package com.halil.ozel.movieparadise.ui.base;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.halil.ozel.movieparadise.R;

/**
 * Plain selector backgrounds for focusable views that live outside Leanback rows (chips etc.).
 * Cards use the Leanback focus highlight configured in {@link TvRows}.
 */
public final class TvFocusHelper {

    private TvFocusHelper() {
    }

    public static void prepareFocusable(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        disableDefaultFocusHighlight(view);
    }

    /**
     * Turns off the platform focus overlay (API 26+) so only our own focus drawables show.
     * Older releases have no default highlight, so there is nothing to disable.
     */
    public static void disableDefaultFocusHighlight(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setDefaultFocusHighlightEnabled(false);
        }
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
