package com.example.yokailist.data.response.animedetail.notused;

import com.google.gson.annotations.SerializedName;

public class DemographicsItem{

	@SerializedName("name")
	private String name;

	@SerializedName("mal_id")
	private int malId;

	@SerializedName("type")
	private String type;

	@SerializedName("url")
	private String url;

	public String getName(){
		return name;
	}

	public int getMalId(){
		return malId;
	}

	public String getType(){
		return type;
	}

	public String getUrl(){
		return url;
	}
}