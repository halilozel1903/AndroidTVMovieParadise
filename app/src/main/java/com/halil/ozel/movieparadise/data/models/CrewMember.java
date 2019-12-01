package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class CrewMember implements Parcelable {

    private int id;
    private String job;
    private String name;
    private String department;

    @Json(name = "profile_path")
    private String profilePath;

    public CrewMember() {
    }

    public int getId() {
        return id;
    }

    public CrewMember setId(int id) {
        this.id = id;
        return this;
    }

    public String getJob() {
        return job;
    }

    public CrewMember setJob(String job) {
        this.job = job;
        return this;
    }

    public String getName() {
        return name;
    }

    public CrewMember setName(String name) {
        this.name = name;
        return this;
    }

    public String getDepartment() {
        return department;
    }

    public CrewMember setDepartment(String department) {
        this.department = department;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public CrewMember setProfilePath(String profilePath) {
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
        dest.writeString(this.job);
        dest.writeString(this.name);
        dest.writeString(this.department);
        dest.writeString(this.profilePath);
    }

    protected CrewMember(Parcel in) {
        this.id = in.readInt();
        this.job = in.readString();
        this.name = in.readString();
        this.department = in.readString();
        this.profilePath = in.readString();
    }

    public static final Creator<CrewMember> CREATOR = new Creator<CrewMember>() {
        @Override
        public CrewMember createFromParcel(Parcel source) {
            return new CrewMember(source);
        }

        @Override
        public CrewMember[] newArray(int size) {
            return new CrewMember[size];
        }
    };
}
