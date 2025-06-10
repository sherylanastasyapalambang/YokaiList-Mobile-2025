package com.example.yokailist.data.response;

import com.google.gson.annotations.SerializedName;

public class Pagination{

	@SerializedName("has_next_page")
	private boolean hasNextPage;

	@SerializedName("last_visible_page")
	private int lastVisiblePage;

	@SerializedName("items")
	private Items items;

	@SerializedName("current_page")
	private int currentPage;

	public boolean isHasNextPage(){
		return hasNextPage;
	}

	public int getLastVisiblePage(){
		return lastVisiblePage;
	}

	public Items getItems(){
		return items;
	}

	public int getCurrentPage(){
		return currentPage;
	}
}