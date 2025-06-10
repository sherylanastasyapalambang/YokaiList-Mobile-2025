package com.example.yokailist.data.response.character.notused;

import com.example.yokailist.data.response.character.Images;
import com.google.gson.annotations.SerializedName;

public class Person{

	@SerializedName("images")
	private Images images;

	@SerializedName("name")
	private String name;

	@SerializedName("mal_id")
	private int malId;

	@SerializedName("url")
	private String url;

	public Images getImages(){
		return images;
	}

	public String getName(){
		return name;
	}

	public int getMalId(){
		return malId;
	}

	public String getUrl(){
		return url;
	}
}