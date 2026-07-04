package com.halil.ozel.movieparadise.ui.base;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

/**
 * Google TV focus önerisi: Leanback yerleşik vurgusu, hafif zoom (1.05x) + glow.
 */
public final class TvRows {

    private TvRows() {
    }

    public static ListRowPresenter listRowPresenter() {
        return new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
    }

    /** Küçük chip/ikon satırları: zoom flicker yapmaz, sadece outline. */
    public static ListRowPresenter tagRowPresenter() {
        return new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE);
    }

    public static VerticalGridPresenter verticalGridPresenter() {
        return new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
    }
}
