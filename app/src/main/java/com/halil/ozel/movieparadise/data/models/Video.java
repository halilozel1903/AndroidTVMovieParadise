package com.halil.ozel.movieparadise.data.models;

public class Video {

    private String id;
    private String key;
    private String site;
    private String type;
    private String name;

    public Video() {
    }

    public String getId() {
        return id;
    }

    public Video setId(String id) {
        this.id = id;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Video setKey(String key) {
        this.key = key;
        return this;
    }

    public String getSite() {
        return site;
    }

    public Video setSite(String site) {
        this.site = site;
        return this;
    }

    public String getType() {
        return type;
    }

    public Video setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public Video setName(String name) {
        this.name = name;
        return this;
    }
}
