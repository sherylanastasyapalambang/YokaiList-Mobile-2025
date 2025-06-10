package com.example.yokailist.data.response.animedetail;

import com.google.gson.annotations.SerializedName;

public class Aired{

	@SerializedName("from")
	private String from; // ambil startdatenya disini. contoh data 'from': "2023-09-29T00:00:00+00:00"

	@SerializedName("to")
	private String to;

	@SerializedName("string")
	private String date;

	public String getFrom(){
		return from;
	}

	public String getTo(){
		return to;
	}

	public String getDate(){
		return date;
	}
}