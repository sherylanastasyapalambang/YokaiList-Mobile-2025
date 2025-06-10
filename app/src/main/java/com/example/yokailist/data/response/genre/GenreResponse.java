package com.example.yokailist.data.response.genre;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GenreResponse{

	@SerializedName("data")
	private List<Genre> data;

	public List<Genre> getData(){
		return data;
	}
}