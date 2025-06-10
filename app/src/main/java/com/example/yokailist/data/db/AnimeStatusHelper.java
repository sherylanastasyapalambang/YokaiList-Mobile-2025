package com.example.yokailist.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnimeStatusHelper {
    private static final String DATABASE_TABLE = DatabaseContract.TABLE_NAME;
    private static DatabaseHelper dataBaseHelper;
    private static SQLiteDatabase database;
    private static AnimeStatusHelper INSTANCE;

    private AnimeStatusHelper(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }

    public static AnimeStatusHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AnimeStatusHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void close() {
        dataBaseHelper.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    public Cursor queryAll() {
        return database.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseContract.AnimeStatusColumns.UPDATED_DATE + " DESC"
        );
    }

    public Cursor queryById(String id) {
        return database.query(
                DATABASE_TABLE,
                null,
                DatabaseContract.AnimeStatusColumns._ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null
        );
    }

    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int update(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, DatabaseContract.AnimeStatusColumns._ID
                + " = ?", new String[]{id});
    }

    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE, DatabaseContract.AnimeStatusColumns._ID + " = ?",
                new String[]{id});
    }

    public Cursor queryByStatus(int status) {
        return database.query(
                DATABASE_TABLE,
                null,
                DatabaseContract.AnimeStatusColumns.STATUS + " = ?",
                new String[]{String.valueOf(status)},
                null,
                null,
                DatabaseContract.AnimeStatusColumns.UPDATED_DATE + " DESC"
        );
    }

    public int getAnimeStatusIdByAnimeId(int animeId) {
        Cursor cursor = database.query(
                DATABASE_TABLE,
                new String[]{DatabaseContract.AnimeStatusColumns._ID},
                DatabaseContract.AnimeStatusColumns.ANIME_ID + " = ?",
                new String[]{String.valueOf(animeId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.AnimeStatusColumns._ID));
            cursor.close();
            return id;
        }

        if (cursor != null) {
            cursor.close();
        }

        return -1;
    }
}
