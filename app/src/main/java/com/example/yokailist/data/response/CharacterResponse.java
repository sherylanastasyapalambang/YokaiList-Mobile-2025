package com.example.yokailist.data.response;

import java.util.List;

import com.example.yokailist.data.response.character.DataItem;
import com.google.gson.annotations.SerializedName;

public class CharacterResponse{

	@SerializedName("data")
	private List<DataItem> data;

	public List<DataItem> getData(){
		return data;
	}
}