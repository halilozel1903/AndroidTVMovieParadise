package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/10/16.
 */

public class Person implements Parcelable {

    private int id;
    private String biography;
    private String birthday;
    private String deathday;
    private int gender;
    private String homepage;
    private String name;
    private float popularity;
    private boolean adult;

    @SerializedName("profile_path")
    private String profilePath;

    @SerializedName("place_of_birth")
    private String placeOfBirth;

    public Person() {
    }

    public int getId() {
        return id;
    }

    public String getBiography() {
        return biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public int getGender() {
        return gender;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getName() {
        return name;
    }

    public float getPopularity() {
        return popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.biography);
        dest.writeString(this.birthday);
        dest.writeString(this.deathday);
        dest.writeInt(this.gender);
        dest.writeString(this.homepage);
        dest.writeString(this.name);
        dest.writeFloat(this.popularity);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.profilePath);
        dest.writeString(this.placeOfBirth);
    }

    protected Person(Parcel in) {
        this.id = in.readInt();
        this.biography = in.readString();
        this.birthday = in.readString();
        this.deathday = in.readString();
        this.gender = in.readInt();
        this.homepage = in.readString();
        this.name = in.readString();
        this.popularity = in.readFloat();
        this.adult = in.readByte() != 0;
        this.profilePath = in.readString();
        this.placeOfBirth = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
