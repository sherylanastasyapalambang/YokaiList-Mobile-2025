package com.example.yokailist.data.response;

import com.example.yokailist.data.response.animedetail.Anime;
import com.google.gson.annotations.SerializedName;

public class AnimeDetailResponse{

	@SerializedName("data")
	private Anime anime;

	public Anime getData(){
		return anime;
	}
}