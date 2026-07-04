package com.halil.ozel.movieparadise.data.models;

import com.google.gson.annotations.SerializedName;

public class PersonImage {

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("aspect_ratio")
    private double aspectRatio;

    public String getFilePath() {
        return filePath;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }
}
