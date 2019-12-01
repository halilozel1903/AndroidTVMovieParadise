package com.halil.ozel.movieparadise.ui.base;

import android.graphics.Color;
import android.support.v7.graphics.Palette;

import com.halil.ozel.movieparadise.data.models.PaletteColors;

public class PaletteUtils {

    public static PaletteColors getPaletteColors(Palette palette) {
        PaletteColors colors = new PaletteColors();

        //figuring out toolbar palette color in order of preference
        if (palette.getDarkVibrantSwatch() != null) {
            colors.setToolbarBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
            colors.setTextColor(palette.getDarkVibrantSwatch().getBodyTextColor());
            colors.setTitleColor(palette.getDarkVibrantSwatch().getTitleTextColor());
        } else if (palette.getDarkMutedSwatch() != null) {
            colors.setToolbarBackgroundColor(palette.getDarkMutedSwatch().getRgb());
            colors.setTextColor(palette.getDarkMutedSwatch().getBodyTextColor());
            colors.setTitleColor(palette.getDarkMutedSwatch().getTitleTextColor());
        } else if (palette.getVibrantSwatch() != null) {
            colors.setToolbarBackgroundColor(palette.getVibrantSwatch().getRgb());
            colors.setTextColor(palette.getVibrantSwatch().getBodyTextColor());
            colors.setTitleColor(palette.getVibrantSwatch().getTitleTextColor());
        }

        //set the status bar color to be a darker version of the toolbar background Color;
        if (colors.getToolbarBackgroundColor() != 0) {
            float[] hsv = new float[3];
            int color = colors.getToolbarBackgroundColor();
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            colors.setStatusBarColor(Color.HSVToColor(hsv));
        }

        return colors;
    }
}
