package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class that mirrors the structure of a TV show returned by the
 * TheMovieDb API.
 */
public class TvShow implements Parcelable {

    private String id;

    @SerializedName("poster_path")
    private String posterPath;

    private String overview;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("genre_ids")
    private List<String> genreIds;

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("original_language")
    private String originalLanguage;

    /** Name of the TV show. */
    @SerializedName("name")
    private String name;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("vote_average")
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

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getName() {
        return name;
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
