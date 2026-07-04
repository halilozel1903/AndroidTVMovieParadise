package com.halil.ozel.movieparadise.ui.detail;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.halil.ozel.movieparadise.data.models.Video;
import com.halil.ozel.movieparadise.data.models.VideoResponse;
import com.halil.ozel.movieparadise.ui.player.PlayerActivity;

import java.util.List;

public final class TrailerHelper {

    public static final String EXTRA_VIDEO_ID = "videoId";
    public static final int ACTION_TRAILER = 0;
    public static final int ACTION_RETRY_DETAILS = 1;

    private TrailerHelper() {
    }

    @Nullable
    public static String findYoutubeTrailerId(@Nullable VideoResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return null;
        }
        List<Video> videos = response.getResults();
        String id = findVideoKey(videos, "official");
        if (id == null) {
            id = findVideoKey(videos, "trailer");
        }
        if (id == null) {
            id = findVideoKeyByType(videos, "trailer");
        }
        return id;
    }

    public static Intent createPlayerIntent(Context context, String videoId) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_VIDEO_ID, videoId);
        return intent;
    }

    @Nullable
    private static String findVideoKey(List<Video> videos, String keyword) {
        return videos.stream()
                .filter(v -> isYoutubeVideo(v)
                        && v.getName() != null
                        && v.getName().toLowerCase().contains(keyword))
                .map(Video::getKey)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    private static String findVideoKeyByType(List<Video> videos, String keyword) {
        return videos.stream()
                .filter(v -> isYoutubeVideo(v)
                        && v.getType() != null
                        && v.getType().toLowerCase().contains(keyword))
                .map(Video::getKey)
                .findFirst()
                .orElse(null);
    }

    private static boolean isYoutubeVideo(Video video) {
        return video != null
                && video.getKey() != null
                && video.getSite() != null
                && "youtube".equalsIgnoreCase(video.getSite());
    }
}
