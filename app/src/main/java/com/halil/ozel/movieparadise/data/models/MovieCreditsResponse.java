package com.halil.ozel.movieparadise.data.models;

import java.util.List;

public class MovieCreditsResponse {
    private int id;
    private List<Movie> cast;

    public int getId() {
        return id;
    }

    public MovieCreditsResponse setId(int id) {
        this.id = id;
        return this;
    }

    public List<Movie> getCast() {
        return cast;
    }

    public MovieCreditsResponse setCast(List<Movie> cast) {
        this.cast = cast;
        return this;
    }
}
