package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;


public class Movie implements Parcelable {

    private String id;

    @Json(name = "poster_path")
    private String posterPath;

    private boolean adult;
    private String overview;

    @Json(name = "release_date")
    private String releaseDate;

    @Json(name = "genre_ids")
    private List<String> genreIds;

    @Json(name = "original_title")
    private String originalTitle;

    @Json(name = "original_language")
    private String originalLanguage;

    private String title;

    @Json(name = "backdrop_path")
    private String backdropPath;

    private float popularity;

    @Json(name = "vote_count")
    private int voteCount;

    private boolean video;

    @Json(name = "vote_average")
    private float voteAverage;

    public Movie() {
    }

    public String getId() {
        return id;
    }

    public Movie setId(String id) {
        this.id = id;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Movie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public boolean isAdult() {
        return adult;
    }

    public Movie setAdult(boolean adult) {
        this.adult = adult;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public Movie setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Movie setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public Movie setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
        return this;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public Movie setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public Movie setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Movie setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public float getPopularity() {
        return popularity;
    }

    public Movie setPopularity(float popularity) {
        this.popularity = popularity;
        return this;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public Movie setVoteCount(int voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public boolean isVideo() {
        return video;
    }

    public Movie setVideo(boolean video) {
        this.video = video;
        return this;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public Movie setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.posterPath);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeStringList(this.genreIds);
        dest.writeString(this.originalTitle);
        dest.writeString(this.originalLanguage);
        dest.writeString(this.title);
        dest.writeString(this.backdropPath);
        dest.writeFloat(this.popularity);
        dest.writeInt(this.voteCount);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.voteAverage);
    }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.posterPath = in.readString();
        this.adult = in.readByte() != 0;
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.genreIds = in.createStringArrayList();
        this.originalTitle = in.readString();
        this.originalLanguage = in.readString();
        this.title = in.readString();
        this.backdropPath = in.readString();
        this.popularity = in.readFloat();
        this.voteCount = in.readInt();
        this.video = in.readByte() != 0;
        this.voteAverage = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
