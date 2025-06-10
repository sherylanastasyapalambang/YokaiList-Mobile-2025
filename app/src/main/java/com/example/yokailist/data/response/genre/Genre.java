package com.example.yokailist.data.response.genre;

import com.google.gson.annotations.SerializedName;

public class Genre {

	@SerializedName("name")
	private String name;

	@SerializedName("count")
	private int count;

	@SerializedName("mal_id")
	private int id;

	@SerializedName("url")
	private String url;

	public String getName(){
		return name;
	}

	public int getCount(){
		return count;
	}

	public int getId(){
		return id;
	}

	public String getUrl(){
		return url;
	}
}