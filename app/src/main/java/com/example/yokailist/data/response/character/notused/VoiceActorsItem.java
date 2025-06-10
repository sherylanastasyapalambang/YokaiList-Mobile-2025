package com.example.yokailist.data.response.character.notused;

import com.google.gson.annotations.SerializedName;

public class VoiceActorsItem{

	@SerializedName("person")
	private Person person;

	@SerializedName("language")
	private String language;

	public Person getPerson(){
		return person;
	}

	public String getLanguage(){
		return language;
	}
}