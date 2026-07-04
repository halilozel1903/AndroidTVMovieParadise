package com.halil.ozel.movieparadise.ui.detail;

import androidx.annotation.Nullable;

import com.halil.ozel.movieparadise.data.models.PersonImage;
import com.halil.ozel.movieparadise.data.models.PersonImagesResponse;

import java.util.List;

public final class PersonImageHelper {

    private static final double PORTRAIT_MAX_RATIO = 0.85;
    private static final double BACKDROP_MIN_RATIO = 1.2;

    private PersonImageHelper() {
    }

    @Nullable
    public static String pickPortraitPath(@Nullable PersonImagesResponse images,
                                            @Nullable String fallbackProfilePath) {
        String fromImages = pickBestPath(images, PORTRAIT_MAX_RATIO, false, null);
        if (fromImages != null) {
            return fromImages;
        }
        return hasText(fallbackProfilePath) ? fallbackProfilePath : null;
    }

    @Nullable
    public static String pickBackdropPath(@Nullable PersonImagesResponse images,
                                          @Nullable String portraitPath) {
        return pickBestPath(images, BACKDROP_MIN_RATIO, true, portraitPath);
    }

    @Nullable
    private static String pickBestPath(@Nullable PersonImagesResponse images,
                                       double threshold,
                                       boolean landscape,
                                       @Nullable String excludePath) {
        if (images == null || images.getProfiles() == null) {
            return null;
        }
        List<PersonImage> profiles = images.getProfiles();
        String bestPath = null;
        double bestRatio = landscape ? 0 : Double.MAX_VALUE;

        for (PersonImage image : profiles) {
            if (image == null || !hasText(image.getFilePath())) {
                continue;
            }
            String path = image.getFilePath();
            if (path.equals(excludePath)) {
                continue;
            }
            double ratio = image.getAspectRatio();
            if (landscape) {
                if (ratio >= threshold && ratio > bestRatio) {
                    bestRatio = ratio;
                    bestPath = path;
                }
            } else if (ratio <= threshold && ratio < bestRatio) {
                bestRatio = ratio;
                bestPath = path;
            }
        }
        return bestPath;
    }

    private static boolean hasText(@Nullable String value) {
        return value != null && !value.trim().isEmpty();
    }
}
