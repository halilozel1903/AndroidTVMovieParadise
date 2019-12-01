package com.halil.ozel.movieparadise.data.models;

import java.util.List;



public class CreditsResponse {

    private int id;
    private List<CastMember> cast;
    private List<CrewMember> crew;

    public CreditsResponse() {
    }

    public int getId() {
        return id;
    }

    public CreditsResponse setId(int id) {
        this.id = id;
        return this;
    }

    public List<CastMember> getCast() {
        return cast;
    }

    public CreditsResponse setCast(List<CastMember> cast) {
        this.cast = cast;
        return this;
    }

    public List<CrewMember> getCrew() {
        return crew;
    }

    public CreditsResponse setCrew(List<CrewMember> crew) {
        this.crew = crew;
        return this;
    }
}
