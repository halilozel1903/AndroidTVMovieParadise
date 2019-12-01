package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;


public class MovieDetails implements Parcelable {

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

    @Json(name = "release_date")
    private String releaseDate;

    @Json(name = "poster_path")
    private String posterPath;

    @Json(name = "original_title")
    private String originalTitle;

    @Json(name = "original_language")
    private String originalLanguage;

    @Json(name = "backdrop_path")
    private String backdropPath;

    @Json(name = "vote_count")
    private int voteCount;

    @Json(name = "vote_average")
    private float voteAverage;

    @Json(name = "imdb_id")
    private String imdbId;

    private PaletteColors paletteColors;
    private String director;

    public MovieDetails() {
    }

    public boolean isAdult() {
        return adult;
    }

    public MovieDetails setAdult(boolean adult) {
        this.adult = adult;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MovieDetails setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public boolean isVideo() {
        return video;
    }

    public MovieDetails setVideo(boolean video) {
        this.video = video;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public MovieDetails setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MovieDetails setTitle(String title) {
        this.title = title;
        return this;
    }

    public float getPopularity() {
        return popularity;
    }

    public MovieDetails setPopularity(float popularity) {
        this.popularity = popularity;
        return this;
    }

    public int getBudget() {
        return budget;
    }

    public MovieDetails setBudget(int budget) {
        this.budget = budget;
        return this;
    }

    public int getRuntime() {
        return runtime;
    }

    public MovieDetails setRuntime(int runtime) {
        this.runtime = runtime;
        return this;
    }

    public int getRevenue() {
        return revenue;
    }

    public MovieDetails setRevenue(int revenue) {
        this.revenue = revenue;
        return this;
    }

    public String getTagline() {
        return tagline;
    }

    public MovieDetails setTagline(String tagline) {
        this.tagline = tagline;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public MovieDetails setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public MovieDetails setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public MovieDetails setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public MovieDetails setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public MovieDetails setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public MovieDetails setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public MovieDetails setVoteCount(int voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public MovieDetails setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    public String getImdbId() {
        return imdbId;
    }

    public MovieDetails setImdbId(String imdbId) {
        this.imdbId = imdbId;
        return this;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.overview);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.genres);
        dest.writeString(this.title);
        dest.writeFloat(this.popularity);
        dest.writeInt(this.budget);
        dest.writeInt(this.runtime);
        dest.writeInt(this.revenue);
        dest.writeString(this.tagline);
        dest.writeString(this.status);
        dest.writeString(this.releaseDate);
        dest.writeString(this.posterPath);
        dest.writeString(this.originalTitle);
        dest.writeString(this.originalLanguage);
        dest.writeString(this.backdropPath);
        dest.writeInt(this.voteCount);
        dest.writeFloat(this.voteAverage);
        dest.writeString(this.imdbId);
        dest.writeParcelable(this.paletteColors, flags);
        dest.writeString(this.director);
    }

    protected MovieDetails(Parcel in) {
        this.adult = in.readByte() != 0;
        this.overview = in.readString();
        this.video = in.readByte() != 0;
        this.genres = in.createTypedArrayList(Genre.CREATOR);
        this.title = in.readString();
        this.popularity = in.readFloat();
        this.budget = in.readInt();
        this.runtime = in.readInt();
        this.revenue = in.readInt();
        this.tagline = in.readString();
        this.status = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.originalTitle = in.readString();
        this.originalLanguage = in.readString();
        this.backdropPath = in.readString();
        this.voteCount = in.readInt();
        this.voteAverage = in.readFloat();
        this.imdbId = in.readString();
        this.paletteColors = in.readParcelable(PaletteColors.class.getClassLoader());
        this.director = in.readString();
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel source) {
            return new MovieDetails(source);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
}
