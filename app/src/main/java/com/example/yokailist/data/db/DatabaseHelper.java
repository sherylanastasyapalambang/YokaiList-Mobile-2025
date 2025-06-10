package com.example.yokailist.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "anime_status.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_ANIME_STATUS=
            String.format(
                    "CREATE TABLE %s" +
                            " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " %s INTEGER NOT NULL," +
                            " %s INTEGER NOT NULL," +
                            " %s INTEGER NOT NULL," +
                            " %s INTEGER NOT NULL," +
                            " %s TEXT," +
                            " %s TEXT," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT NOT NULL," +
                            " %s INTEGER NOT NULL," +
                            " %s TEXT NOT NULL)",
                    DatabaseContract.TABLE_NAME,
                    DatabaseContract.AnimeStatusColumns._ID,
                    DatabaseContract.AnimeStatusColumns.ANIME_ID,
                    DatabaseContract.AnimeStatusColumns.SCORE,
                    DatabaseContract.AnimeStatusColumns.PROGRESS,
                    DatabaseContract.AnimeStatusColumns.STATUS,
                    DatabaseContract.AnimeStatusColumns.START_DATE,
                    DatabaseContract.AnimeStatusColumns.END_DATE,
                    DatabaseContract.AnimeStatusColumns.TITLE,
                    DatabaseContract.AnimeStatusColumns.IMAGE_URL,
                    DatabaseContract.AnimeStatusColumns.TYPE,
                    DatabaseContract.AnimeStatusColumns.SEASON,
                    DatabaseContract.AnimeStatusColumns.EPISODES,
                    DatabaseContract.AnimeStatusColumns.UPDATED_DATE
            );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_ANIME_STATUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
