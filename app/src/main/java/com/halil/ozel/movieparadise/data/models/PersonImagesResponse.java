package com.halil.ozel.movieparadise.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonImagesResponse {

    @SerializedName("profiles")
    private List<PersonImage> profiles;

    public List<PersonImage> getProfiles() {
        return profiles;
    }
}
