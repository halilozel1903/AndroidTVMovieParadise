package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Simple wrapper class used to parse paged TV show responses from the API.
 */
public class TvShowResponse implements Parcelable {

    private int page;
    private List<TvShow> results;

    @Json(name = "total_pages")
    private int totalPages;

    @Json(name = "total_results")
    private int totalResults;

    public int getPage() {
        return page;
    }

    public TvShowResponse setPage(int page) {
        this.page = page;
        return this;
    }

    public List<TvShow> getResults() {
        return results;
    }

    public TvShowResponse setResults(List<TvShow> results) {
        this.results = results;
        return this;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public TvShowResponse setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public TvShowResponse setTotalResults(int totalResults) {
        this.totalResults = totalResults;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeTypedList(results);
        dest.writeInt(totalPages);
        dest.writeInt(totalResults);
    }

    public TvShowResponse() {
    }

    protected TvShowResponse(Parcel in) {
        this.page = in.readInt();
        this.results = in.createTypedArrayList(TvShow.CREATOR);
        this.totalPages = in.readInt();
        this.totalResults = in.readInt();
    }

    public static final Creator<TvShowResponse> CREATOR = new Creator<TvShowResponse>() {
        @Override
        public TvShowResponse createFromParcel(Parcel source) {
            return new TvShowResponse(source);
        }

        @Override
        public TvShowResponse[] newArray(int size) {
            return new TvShowResponse[size];
        }
    };
}
