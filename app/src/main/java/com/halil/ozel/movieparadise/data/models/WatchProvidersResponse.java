package com.halil.ozel.movieparadise.data.models;

import java.util.Map;

public class WatchProvidersResponse {

    private int id;
    private Map<String, CountryWatchProviders> results;

    public int getId() {
        return id;
    }

    public Map<String, CountryWatchProviders> getResults() {
        return results;
    }
}
