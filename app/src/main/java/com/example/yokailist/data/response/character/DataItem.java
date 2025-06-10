package com.example.yokailist.data.response.character;

import java.util.List;

import com.example.yokailist.data.response.character.notused.VoiceActorsItem;
import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("character")
	private Character character;

	@SerializedName("role")
	private String role;

	@SerializedName("voice_actors")
	private List<VoiceActorsItem> voiceActors;

	public Character getCharacter(){
		return character;
	}

	public String getRole(){
		return role;
	}

	public List<VoiceActorsItem> getVoiceActors(){
		return voiceActors;
	}
}