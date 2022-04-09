package com.halil.ozel.movieparadise.ui.base;

import android.graphics.Color;

import androidx.palette.graphics.Palette;

import com.halil.ozel.movieparadise.data.models.PaletteColors;

public class PaletteUtils {

    public static PaletteColors getPaletteColors(Palette palette) {
        PaletteColors paletteColors = new PaletteColors();

        //figuring out toolbar palette color in order of preference
        if (palette.getDarkVibrantSwatch() != null) {
            paletteColors.setToolbarBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
            paletteColors.setTextColor(palette.getDarkVibrantSwatch().getBodyTextColor());
            paletteColors.setTitleColor(palette.getDarkVibrantSwatch().getTitleTextColor());
        } else if (palette.getDarkMutedSwatch() != null) {
            paletteColors.setToolbarBackgroundColor(palette.getDarkMutedSwatch().getRgb());
            paletteColors.setTextColor(palette.getDarkMutedSwatch().getBodyTextColor());
            paletteColors.setTitleColor(palette.getDarkMutedSwatch().getTitleTextColor());
        } else if (palette.getVibrantSwatch() != null) {
            paletteColors.setToolbarBackgroundColor(palette.getVibrantSwatch().getRgb());
            paletteColors.setTextColor(palette.getVibrantSwatch().getBodyTextColor());
            paletteColors.setTitleColor(palette.getVibrantSwatch().getTitleTextColor());
        }

        //set the status bar color to be a darker version of the toolbar background Color;
        if (paletteColors.getToolbarBackgroundColor() != 0) {
            float[] hsv = new float[3];
            int color = paletteColors.getToolbarBackgroundColor();
            Color.colorToHSV(color, hsv);
            // value component
            hsv[2] *= 0.8f;
            paletteColors.setStatusBarColor(Color.HSVToColor(hsv));
        }

        return paletteColors;
    }
}
