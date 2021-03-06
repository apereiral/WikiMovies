package com.example.lucas.wikimovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucas on 9/19/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " UNIQUE ( " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " ) ON CONFLICT REPLACE" +
                ");";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieContract.FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " UNIQUE ( " + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " ) ON CONFLICT REPLACE" +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
