package com.example.yokailist.data.response.animedetail;

import com.google.gson.annotations.SerializedName;

public class Webp{

	@SerializedName("large_image_url")
	private String largeImageUrl;

	@SerializedName("small_image_url")
	private String smallImageUrl;

	@SerializedName("image_url")
	private String imageUrl;

	public String getLargeImageUrl(){
		return largeImageUrl;
	}

	public String getSmallImageUrl(){
		return smallImageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}
}