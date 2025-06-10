package com.example.yokailist.data.response.animedetail;

import com.google.gson.annotations.SerializedName;

public class Images{

	@SerializedName("jpg")
	private Jpg jpg;

	@SerializedName("webp")
	private Webp webp;

	public Jpg getJpg(){
		return jpg;
	}

	public Webp getWebp(){
		return webp;
	}
}