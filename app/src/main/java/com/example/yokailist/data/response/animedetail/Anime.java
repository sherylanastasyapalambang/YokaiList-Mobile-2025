package com.example.yokailist.data.response.animedetail;

import java.util.List;

import com.example.yokailist.data.response.animedetail.notused.DemographicsItem;
import com.example.yokailist.data.response.animedetail.notused.ExplicitGenresItem;
import com.example.yokailist.data.response.animedetail.notused.ThemesItem;
import com.google.gson.annotations.SerializedName;

public class Anime {
	@SerializedName("title_japanese")
	private String titleJapanese;

	@SerializedName("favorites")
	private int favorites;

	@SerializedName("broadcast")
	private Broadcast broadcast;

	@SerializedName("year")
	private Integer year;

	@SerializedName("rating")
	private String rating;

	@SerializedName("scored_by")
	private int scoredBy;

//	@SerializedName("title_synonyms")
//	private List<String> titleSynonyms;

	@SerializedName("source")
	private String source;

	@SerializedName("title")
	private String title;

	@SerializedName("type")
	private String type;

	@SerializedName("trailer")
	private Trailer trailer;

	@SerializedName("duration")
	private String duration;

	@SerializedName("score")
	private double score;

	@SerializedName("themes")
	private List<ThemesItem> themes;

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("genres")
	private List<GenresItem> genres;

	@SerializedName("popularity")
	private int popularity;

	@SerializedName("members")
	private int members;

	@SerializedName("title_english")
	private String titleEnglish;

	@SerializedName("rank")
	private int rank;

	@SerializedName("season")
	private String season;

	@SerializedName("airing")
	private boolean airing;

	@SerializedName("episodes")
	private int episodes;

	@SerializedName("aired")
	private Aired aired;

	@SerializedName("images")
	private Images images;

	@SerializedName("studios")
	private List<StudiosItem> studios;

	@SerializedName("mal_id")
	private int id;

	@SerializedName("synopsis")
	private String synopsis;

	@SerializedName("explicit_genres")
	private List<ExplicitGenresItem> explicitGenres;

	@SerializedName("licensors")
	private List<LicensorsItem> licensors;

	@SerializedName("url")
	private String url;

	@SerializedName("producers")
	private List<ProducersItem> producers;

	@SerializedName("background")
	private String background;

	@SerializedName("status")
	private String status;

	@SerializedName("demographics")
	private List<DemographicsItem> demographics;

	public String getTitleJapanese(){
		return titleJapanese;
	}

	public int getFavorites(){
		return favorites;
	}

	public Broadcast getBroadcast(){
		return broadcast;
	}

	public Integer getYear(){
		return year;
	}

	public String getRating(){
		return rating;
	}

	public int getScoredBy(){
		return scoredBy;
	}

//	public List<String> getTitleSynonyms(){
//		return titleSynonyms;
//	}

	public String getSource(){
		return source;
	}

	public String getTitle(){
		return title;
	}

	public String getType(){
		return type;
	}

	public Trailer getTrailer(){
		return trailer;
	}

	public String getDuration(){
		return duration;
	}

	public double getScore(){
		return score;
	}

	public List<ThemesItem> getThemes(){
		return themes;
	}

	public boolean isApproved(){
		return approved;
	}

	public List<GenresItem> getGenres(){
		return genres;
	}

	public int getPopularity(){
		return popularity;
	}

	public int getMembers(){
		return members;
	}

	public String getTitleEnglish(){
		return titleEnglish;
	}

	public int getRank(){
		return rank;
	}

	public String getSeason(){
		return season;
	}

	public boolean isAiring(){
		return airing;
	}

	public int getEpisodes(){
		return episodes;
	}

	public Aired getAired(){
		return aired;
	}

	public Images getImages(){
		return images;
	}

	public List<StudiosItem> getStudios(){
		return studios;
	}

	public int getId(){
		return id;
	}

	public String getSynopsis(){
		return synopsis;
	}

	public List<ExplicitGenresItem> getExplicitGenres(){
		return explicitGenres;
	}

	public List<LicensorsItem> getLicensors(){
		return licensors;
	}

	public String getUrl(){
		return url;
	}

	public List<ProducersItem> getProducers(){
		return producers;
	}

	public String getBackground(){
		return background;
	}

	public String getStatus(){
		return status;
	}

	public List<DemographicsItem> getDemographics(){
		return demographics;
	}
}