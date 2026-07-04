package com.halil.ozel.movieparadise.data.models;

import com.google.gson.annotations.SerializedName;

public class WatchProvider {

    @SerializedName("logo_path")
    private String logoPath;

    @SerializedName("provider_id")
    private int providerId;

    @SerializedName("provider_name")
    private String providerName;

    public String getLogoPath() {
        return logoPath;
    }

    public int getProviderId() {
        return providerId;
    }

    public String getProviderName() {
        return providerName;
    }
}
