package com.example.yokailist.data.response.animedetail;

import com.google.gson.annotations.SerializedName;

public class Trailer{

	@SerializedName("embed_url")
	private String embedUrl;

	@SerializedName("youtube_id")
	private String youtubeId;

	@SerializedName("url")
	private String url;

	@SerializedName("images")
	private TrailerImages images;

	public String getEmbedUrl(){
		return embedUrl;
	}

	public String getYoutubeId(){
		return youtubeId;
	}

	public String getUrl(){
		return url;
	}

	public TrailerImages getImages(){
		return images;
	}

}