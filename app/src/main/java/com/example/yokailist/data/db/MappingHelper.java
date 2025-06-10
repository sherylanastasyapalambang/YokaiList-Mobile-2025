package com.example.yokailist.data.db;

import android.database.Cursor;

import com.example.yokailist.data.models.AnimeStatus;

import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<AnimeStatus> mapCursorToArrayList(Cursor cursor) {
        ArrayList<AnimeStatus> animeStatusList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns._ID));
            int animeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.ANIME_ID));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.SCORE));
            int progress = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.PROGRESS));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.STATUS));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.END_DATE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.TITLE));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.IMAGE_URL));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.TYPE));
            String season = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.SEASON));
            int episodes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.EPISODES));
            String updatedDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.UPDATED_DATE));

            animeStatusList.add(new AnimeStatus(id, animeId, score, progress, status, startDate, endDate, title, imageUrl, type, season, episodes, updatedDate));
        }
        return animeStatusList;
    }

    public static AnimeStatus mapCursorToObject(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns._ID));
            int animeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.ANIME_ID));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.SCORE));
            int progress = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.PROGRESS));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.STATUS));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.END_DATE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.TITLE));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.IMAGE_URL));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.TYPE));
            String season = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.SEASON));
            int episodes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.EPISODES));
            String updatedDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns.UPDATED_DATE));

            cursor.close();
            return new AnimeStatus(id, animeId, score, progress, status, startDate, endDate, title, imageUrl, type, season, episodes, updatedDate);
        }

        if (cursor != null) cursor.close();
        return null;
    }

}
