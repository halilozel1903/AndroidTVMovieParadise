package com.halil.ozel.movieparadise.data.models;

import java.util.List;


public class CreditsResponse {

    private int id;
    private List<CastMember> cast;
    private List<CrewMember> crew;

    public CreditsResponse() {
    }

    public List<CastMember> getCast() {
        return cast;
    }

    public List<CrewMember> getCrew() {
        return crew;
    }
}
