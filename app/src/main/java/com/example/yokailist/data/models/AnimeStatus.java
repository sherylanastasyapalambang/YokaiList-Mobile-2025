package com.example.yokailist.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AnimeStatus implements Parcelable {
    private int id;
    private int animeId = 0;
    private int score = 0;
    private int progress = 0;
    private int status = 0; // 1 watching, 2 completed, 3 on hold, 4 dropped, 5 plan to watch
    private String startDate;
    private String endDate;
    private String title;
    private String imageUrl;
    private String type;
    private String season;
    private int episodes;
    private String updatedDate;

    public AnimeStatus() {
    }

    public AnimeStatus(int id, int animeId, int score, int progress, int status, String startDate, String endDate, String title, String imageUrl, String type, String season, int episodes, String updatedDate) {
        this.id = id;
        this.animeId = animeId;
        this.score = score;
        this.progress = progress;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.imageUrl = imageUrl;
        this.type = type;
        this.season = season;
        this.episodes = episodes;
        this.updatedDate = updatedDate;
    }


    protected AnimeStatus(Parcel in) {
        id = in.readInt();
        animeId = in.readInt();
        score = in.readInt();
        progress = in.readInt();
        status = in.readInt();
        startDate = in.readString();
        endDate = in.readString();
        title = in.readString();
        imageUrl = in.readString();
        type = in.readString();
        season = in.readString();
        episodes = in.readInt();
        updatedDate = in.readString();
    }

    public static final Creator<AnimeStatus> CREATOR = new Creator<AnimeStatus>() {
        @Override
        public AnimeStatus createFromParcel(Parcel in) {
            return new AnimeStatus(in);
        }

        @Override
        public AnimeStatus[] newArray(int size) {
            return new AnimeStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(animeId);
        dest.writeInt(score);
        dest.writeInt(progress);
        dest.writeInt(status);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeString(type);
        dest.writeString(season);
        dest.writeInt(episodes);
        dest.writeString(updatedDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnimeId() {
        return animeId;
    }

    public void setAnimeId(int animeId) {
        this.animeId = animeId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
