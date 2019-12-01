package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;


public class MovieResponse implements Parcelable {

    private int page;
    private List<Movie> results;

    @Json(name = "total_pages")
    private int totalPages;

    @Json(name = "total_results")
    private int totalResults;

    public int getPage() {
        return page;
    }

    public MovieResponse setPage(int page) {
        this.page = page;
        return this;
    }

    public List<Movie> getResults() {
        return results;
    }

    public MovieResponse setResults(List<Movie> results) {
        this.results = results;
        return this;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public MovieResponse setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public MovieResponse setTotalResults(int totalResults) {
        this.totalResults = totalResults;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeTypedList(this.results);
        dest.writeInt(this.totalPages);
        dest.writeInt(this.totalResults);
    }

    public MovieResponse() {
    }

    protected MovieResponse(Parcel in) {
        this.page = in.readInt();
        this.results = in.createTypedArrayList(Movie.CREATOR);
        this.totalPages = in.readInt();
        this.totalResults = in.readInt();
    }

    public static final Creator<MovieResponse> CREATOR = new Creator<MovieResponse>() {
        @Override
        public MovieResponse createFromParcel(Parcel source) {
            return new MovieResponse(source);
        }

        @Override
        public MovieResponse[] newArray(int size) {
            return new MovieResponse[size];
        }
    };
}
