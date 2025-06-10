package com.example.yokailist.data.response.character.notused;

import com.google.gson.annotations.SerializedName;

public class Jpg{

	@SerializedName("small_image_url")
	private String smallImageUrl;

	@SerializedName("image_url")
	private String imageUrl;

	public String getSmallImageUrl(){
		return smallImageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}
}