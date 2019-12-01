package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

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

    @Json(name = "profile_path")
    private String profilePath;

    @Json(name = "place_of_birth")
    private String placeOfBirth;

    public Person() {
    }

    public int getId() {
        return id;
    }

    public Person setId(int id) {
        this.id = id;
        return this;
    }

    public String getBiography() {
        return biography;
    }

    public Person setBiography(String biography) {
        this.biography = biography;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public Person setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getDeathday() {
        return deathday;
    }

    public Person setDeathday(String deathday) {
        this.deathday = deathday;
        return this;
    }

    public int getGender() {
        return gender;
    }

    public Person setGender(int gender) {
        this.gender = gender;
        return this;
    }

    public String getHomepage() {
        return homepage;
    }

    public Person setHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public float getPopularity() {
        return popularity;
    }

    public Person setPopularity(float popularity) {
        this.popularity = popularity;
        return this;
    }

    public boolean isAdult() {
        return adult;
    }

    public Person setAdult(boolean adult) {
        this.adult = adult;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public Person setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public Person setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
        return this;
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
