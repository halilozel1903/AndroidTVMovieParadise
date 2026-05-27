package com.halil.ozel.movieparadise.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MovieDetails {

    private boolean adult;
    private String overview;
    private boolean video;
    private List<Genre> genres;
    private String title;
    private float popularity;
    private int budget;
    private int runtime;
    private int revenue;
    private String tagline;
    private String status;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("vote_average")
    private float voteAverage;

    @SerializedName("imdb_id")
    private String imdbId;

    private PaletteColors paletteColors;
    private String director;

    public MovieDetails() {
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public boolean isVideo() {
        return video;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getTitle() {
        return title;
    }

    public float getPopularity() {
        return popularity;
    }

    public int getBudget() {
        return budget;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getRevenue() {
        return revenue;
    }

    public String getTagline() {
        return tagline;
    }

    public String getStatus() {
        return status;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getImdbId() {
        return imdbId;
    }

    public PaletteColors getPaletteColors() {
        return paletteColors;
    }

    public MovieDetails setPaletteColors(PaletteColors paletteColors) {
        this.paletteColors = paletteColors;
        return this;
    }

    public String getDirector() {
        return director;
    }

    public MovieDetails setDirector(String director) {
        this.director = director;
        return this;
    }



}
