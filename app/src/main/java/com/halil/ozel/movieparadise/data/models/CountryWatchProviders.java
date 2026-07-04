package com.halil.ozel.movieparadise.data.models;

import java.util.List;

public class CountryWatchProviders {

    private String link;
    private List<WatchProvider> flatrate;
    private List<WatchProvider> rent;
    private List<WatchProvider> buy;

    public String getLink() {
        return link;
    }

    public List<WatchProvider> getFlatrate() {
        return flatrate;
    }

    public List<WatchProvider> getRent() {
        return rent;
    }

    public List<WatchProvider> getBuy() {
        return buy;
    }
}
