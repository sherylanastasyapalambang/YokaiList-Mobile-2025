package com.example.yokailist.data.response.animedetail;

import com.google.gson.annotations.SerializedName;

public class Broadcast{

	@SerializedName("string")
	private String string;

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("time")
	private String time;

	@SerializedName("day")
	private String day;

	public String getString(){
		return string;
	}

	public String getTimezone(){
		return timezone;
	}

	public String getTime(){
		return time;
	}

	public String getDay(){
		return day;
	}
}