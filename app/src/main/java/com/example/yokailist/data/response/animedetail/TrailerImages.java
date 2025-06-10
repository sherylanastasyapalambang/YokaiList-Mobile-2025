package com.example.yokailist.data.response.animedetail;

import com.google.gson.annotations.SerializedName;

public class TrailerImages {

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("large_image_url")
    private String largeImageUrl;

    @SerializedName("maximum_image_url")
    private String maximumImageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public String getMaximumImageUrl() {
        return maximumImageUrl;
    }
}
