package com.example.yokailist.data.response;

import com.example.yokailist.data.response.animedetail.Anime;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeResponse {
    @SerializedName("data")
    private List<Anime> data;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<Anime> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
