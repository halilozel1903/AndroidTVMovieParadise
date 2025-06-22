package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Model class that mirrors the structure of a TV show returned by the
 * TheMovieDb API.
 */
public class TvShow implements Parcelable {

    private String id;

    @Json(name = "poster_path")
    private String posterPath;

    private String overview;

    @Json(name = "first_air_date")
    private String firstAirDate;

    @Json(name = "genre_ids")
    private List<String> genreIds;

    @Json(name = "original_name")
    private String originalName;

    @Json(name = "original_language")
    private String originalLanguage;

    /** Name of the TV show. */
    @Json(name = "name")
    private String name;

    @Json(name = "backdrop_path")
    private String backdropPath;

    @Json(name = "vote_count")
    private int voteCount;

    @Json(name = "vote_average")
    private float voteAverage;

    public TvShow() {
    }

    protected TvShow(Parcel in) {
        id = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        firstAirDate = in.readString();
        genreIds = in.createStringArrayList();
        originalName = in.readString();
        originalLanguage = in.readString();
        name = in.readString();
        backdropPath = in.readString();
        voteCount = in.readInt();
        voteAverage = in.readFloat();
    }

    public static final Creator<TvShow> CREATOR = new Creator<TvShow>() {
        @Override
        public TvShow createFromParcel(Parcel in) {
            return new TvShow(in);
        }

        @Override
        public TvShow[] newArray(int size) {
            return new TvShow[size];
        }
    };

    public String getId() {
        return id;
    }

    public TvShow setId(String id) {
        this.id = id;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public TvShow setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public TvShow setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public TvShow setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
        return this;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public TvShow setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
        return this;
    }

    public String getOriginalName() {
        return originalName;
    }

    public TvShow setOriginalName(String originalName) {
        this.originalName = originalName;
        return this;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public TvShow setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
        return this;
    }

    public String getName() {
        return name;
    }

    public TvShow setName(String name) {
        this.name = name;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public TvShow setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public TvShow setVoteCount(int voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public TvShow setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(firstAirDate);
        dest.writeStringList(genreIds);
        dest.writeString(originalName);
        dest.writeString(originalLanguage);
        dest.writeString(name);
        dest.writeString(backdropPath);
        dest.writeInt(voteCount);
        dest.writeFloat(voteAverage);
    }
}
