package com.halil.ozel.movieparadise.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CrewMember implements Parcelable {

    private int id;
    private String job;
    private String name;
    private String department;

    @SerializedName("profile_path")
    private String profilePath;

    public CrewMember() {
    }

    public int getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
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
