package com.example.lucas.wikimovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucas on 9/19/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        final String SQL_CREATE_TMDB_MOVIE_ID_TABLE = "CREATE TABLE " +
//                MovieContract.TMDBMovieIdEntry.TABLE_NAME + " (" +
//                MovieContract.TMDBMovieIdEntry._ID + " INTEGER PRIMARY KEY, " +
//                MovieContract.TMDBMovieIdEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
//                MovieContract.TMDBMovieIdEntry.COLUMN_TRAILERS_JSON_OBJECT + " TEXT NOT NULL, " +
//                MovieContract.TMDBMovieIdEntry.COLUMN_REVIEWS_JSON_OBJECT + " TEXT NOT NULL " +
//                ");";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
//                MovieContract.MovieEntry.CONLUMN_TMDB_MOVIE_ID_KEY + " INTEGER NOT NULL, " +
//                " FOREIGN KEY (" + MovieContract.MovieEntry.CONLUMN_TMDB_MOVIE_ID_KEY +
//                ") REFERENCES " + MovieContract.TMDBMovieIdEntry.TABLE_NAME + " (" +
//                MovieContract.TMDBMovieIdEntry._ID + ");";
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TRAILERS_JSON_OBJECT + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_REVIEWS_JSON_OBJECT + " TEXT NOT NULL " +
                ");";

//        db.execSQL(SQL_CREATE_TMDB_MOVIE_ID_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
//        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TMDBMovieIdEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
