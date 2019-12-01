package com.halil.ozel.movieparadise.data.models;

import java.util.List;


public class VideoResponse {

    private int id;
    private List<Video> results;

    public VideoResponse() {
    }

    public int getId() {
        return id;
    }

    public VideoResponse setId(int id) {
        this.id = id;
        return this;
    }

    public List<Video> getResults() {
        return results;
    }

    public VideoResponse setResults(List<Video> results) {
        this.results = results;
        return this;
    }
}
