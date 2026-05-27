package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class CastMember implements Parcelable {

    private int id;
    private String character;
    private String name;
    private int order;

    @SerializedName("cast_id")
    private int castId;

    @SerializedName("credit_id")
    private String creditId;

    @SerializedName("profile_path")
    private String profilePath;

    public CastMember() {
    }

    public int getId() {
        return id;
    }

    public int getCastId() {
        return castId;
    }

    public String getCharacter() {
        return character;
    }

    public String getCreditId() {
        return creditId;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getProfilePath() {
        return profilePath;
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
