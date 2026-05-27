package com.halil.ozel.movieparadise.data.models;

import java.util.List;


public class VideoResponse {

    private int id;
    private List<Video> results;

    public VideoResponse() {
    }

    public List<Video> getResults() {
        return results;
    }
}
