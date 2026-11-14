package com.halil.ozel.movieparadise.ui.detail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import com.halil.ozel.movieparadise.data.models.VideoResponse;

import org.junit.Test;

public class TrailerHelperTest {

    private static VideoResponse videos(String resultsJson) {
        return new Gson().fromJson("{\"id\":1,\"results\":" + resultsJson + "}", VideoResponse.class);
    }

    @Test
    public void returnsNullWhenThereIsNothingToPlay() {
        assertNull(TrailerHelper.findYoutubeTrailerId(null));
        assertNull(TrailerHelper.findYoutubeTrailerId(new VideoResponse()));
        assertNull(TrailerHelper.findYoutubeTrailerId(videos("[]")));
    }

    @Test
    public void prefersOfficialTrailerOverOtherVideos() {
        VideoResponse response = videos("["
                + "{\"key\":\"teaser\",\"site\":\"YouTube\",\"type\":\"Teaser\",\"name\":\"Teaser Trailer\"},"
                + "{\"key\":\"official\",\"site\":\"YouTube\",\"type\":\"Trailer\",\"name\":\"Official Trailer\"}"
                + "]");

        assertEquals("official", TrailerHelper.findYoutubeTrailerId(response));
    }

    @Test
    public void fallsBackToVideoTypeWhenNameDoesNotMentionTrailer() {
        VideoResponse response = videos("["
                + "{\"key\":\"clip\",\"site\":\"YouTube\",\"type\":\"Clip\",\"name\":\"Opening scene\"},"
                + "{\"key\":\"typed\",\"site\":\"YouTube\",\"type\":\"Trailer\",\"name\":\"Final look\"}"
                + "]");

        assertEquals("typed", TrailerHelper.findYoutubeTrailerId(response));
    }

    @Test
    public void ignoresVideosThatAreNotHostedOnYoutube() {
        VideoResponse response = videos("["
                + "{\"key\":\"vimeo\",\"site\":\"Vimeo\",\"type\":\"Trailer\",\"name\":\"Official Trailer\"}"
                + "]");

        assertNull(TrailerHelper.findYoutubeTrailerId(response));
    }

    @Test
    public void matchingIsCaseInsensitiveAndLocaleIndependent() {
        java.util.Locale previous = java.util.Locale.getDefault();
        // In Turkish, "I".toLowerCase() is a dotless "ı", which would break a naive match.
        java.util.Locale.setDefault(java.util.Locale.forLanguageTag("tr-TR"));
        try {
            VideoResponse response = videos("["
                    + "{\"key\":\"upper\",\"site\":\"YOUTUBE\",\"type\":\"TRAILER\",\"name\":\"OFFICIAL TRAILER\"}"
                    + "]");

            assertEquals("upper", TrailerHelper.findYoutubeTrailerId(response));
        } finally {
            java.util.Locale.setDefault(previous);
        }
    }
}
