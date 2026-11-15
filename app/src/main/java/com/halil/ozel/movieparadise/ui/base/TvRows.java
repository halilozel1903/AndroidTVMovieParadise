package com.halil.ozel.movieparadise.ui.base;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

/**
 * Google TV focus guidance: built-in Leanback highlight with a subtle zoom (1.05x) and glow.
 */
public final class TvRows {

    private TvRows() {
    }

    public static ListRowPresenter listRowPresenter() {
        return new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
    }

    /** Small chip/icon rows: outline only, no zoom (avoids flicker). */
    public static ListRowPresenter tagRowPresenter() {
        return new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE);
    }

    public static VerticalGridPresenter verticalGridPresenter() {
        return new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
    }
}
