package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;


public class CastMember implements Parcelable {

    private int id;
    private String character;
    private String name;
    private int order;

    @Json(name = "cast_id")
    private int castId;

    @Json(name = "credit_id")
    private String creditId;

    @Json(name = "profile_path")
    private String profilePath;

    public CastMember() {
    }

    public int getId() {
        return id;
    }

    public CastMember setId(int id) {
        this.id = id;
        return this;
    }

    public int getCastId() {
        return castId;
    }

    public CastMember setCastId(int castId) {
        this.castId = castId;
        return this;
    }

    public String getCharacter() {
        return character;
    }

    public CastMember setCharacter(String character) {
        this.character = character;
        return this;
    }

    public String getCreditId() {
        return creditId;
    }

    public CastMember setCreditId(String creditId) {
        this.creditId = creditId;
        return this;
    }

    public String getName() {
        return name;
    }

    public CastMember setName(String name) {
        this.name = name;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public CastMember setOrder(int order) {
        this.order = order;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public CastMember setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.character);
        dest.writeString(this.name);
        dest.writeInt(this.order);
        dest.writeInt(this.castId);
        dest.writeString(this.creditId);
        dest.writeString(this.profilePath);
    }

    protected CastMember(Parcel in) {
        this.id = in.readInt();
        this.character = in.readString();
        this.name = in.readString();
        this.order = in.readInt();
        this.castId = in.readInt();
        this.creditId = in.readString();
        this.profilePath = in.readString();
    }

    public static final Creator<CastMember> CREATOR = new Creator<CastMember>() {
        @Override
        public CastMember createFromParcel(Parcel source) {
            return new CastMember(source);
        }

        @Override
        public CastMember[] newArray(int size) {
            return new CastMember[size];
        }
    };
}
