package com.example.yokailist.data.db;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NAME = "anime_status";

    public static final class AnimeStatusColumns implements BaseColumns {
        public static final String ANIME_ID = "anime_id";
        public static final String SCORE = "score";
        public static final String PROGRESS = "progress";
        public static final String STATUS = "status";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String TITLE = "title";
        public static final String IMAGE_URL = "image_url";
        public static final String TYPE = "type";
        public static final String SEASON = "season";
        public static final String EPISODES = "episodes";
        public static final String UPDATED_DATE = "updated_date";
    }

}
