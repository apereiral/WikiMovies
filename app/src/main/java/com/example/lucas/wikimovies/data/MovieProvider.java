package com.example.lucas.wikimovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Lucas on 9/19/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
//    static final int POSTER = 101;
//    static final int TMDB_MOVIE_ID = 200;

//    // movie.poster_path = ?
//    private static final String sPosterSelection = MovieContract.MovieEntry.TABLE_NAME + "." +
//            MovieContract.MovieEntry.COLUMN_POSTER_PATH + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
//            case MOVIE_DETAILS:
//                return mOpenHelper.getReadableDatabase().query(
//                        MovieContract.MovieDetailsEntry.TABLE_NAME, projection, selection,
//                        selectionArgs, null, null, sortOrder);
//            case POSTER:
//                cursor = getPoster(uri, projection, sortOrder);
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

//    private Cursor getPoster(Uri uri, String[] projection, String sortOrder) {
//        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getPosterPathFromUri(uri)};
//        return mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
//                projection, sPosterSelection, selectionArgs, null, null,
//                sortOrder);
//    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
//            case MOVIE_DETAILS:
//                return MovieContract.MovieDetailsEntry.CONTENT_TYPE;
//            case POSTER:
//                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;
        if (match == MOVIE) {
            long _id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
            if (_id > 0) {
                retUri = MovieContract.MovieEntry.buildMovieUri(_id);
            } else {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) {
            selection = "1";
        }
        if (match == MOVIE) {
            rowsDeleted = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (selection == null) {
            selection = "1";
        }
        if (match == MOVIE) {
            rowsUpdated = database.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                    selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        if (match == MOVIE) {
            database.beginTransaction();
            int retCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        retCount++;
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return retCount;
        } else {
            return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
//        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE_DETAILS, MOVIE_DETAILS);
//        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", POSTER);

        // 3) Return the new matcher!
        return uriMatcher;
    }
}
