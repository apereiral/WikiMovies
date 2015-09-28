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
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITES = 200;
    static final int FAVORITES_WITH_MOVIE_ID = 201;

    // movie._id = ?
    private static final String sIdSelection = MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry._ID + " = ? ";
    // favorites.movie_id = ?
    private static final String sFavoriteMovieIdSelection = MovieContract.FavoriteEntry.TABLE_NAME +
            "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?";

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                cursor = getMovieWithId(uri, projection, sortOrder);
                break;
            case FAVORITES:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case FAVORITES_WITH_MOVIE_ID:
                cursor = getFavoriteWithMovieId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getFavoriteWithMovieId(Uri uri, String[] projection, String sortOrder) {
        String[] selectionArgs = new String[]{MovieContract.FavoriteEntry.getIdFromUri(uri)};
        return mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,
                projection, sFavoriteMovieIdSelection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getMovieWithId(Uri uri, String[] projection, String sortOrder) {
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getIdFromUri(uri)};
        return mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection, sIdSelection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITES_WITH_MOVIE_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;

        switch (match) {
            case MOVIE:
                long _id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    retUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case FAVORITES:
                long _idFav = database.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_idFav > 0) {
                    retUri = MovieContract.FavoriteEntry.buildFavoriteWithIdUri(_idFav);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case MOVIE_WITH_ID:
            default:
                throw new UnsupportedOperationException(
                        "Operation not implemented yet with uri: " + uri);
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

        switch (match) {
            case MOVIE:
                rowsDeleted = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = database.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case FAVORITES_WITH_MOVIE_ID:
                rowsDeleted = deleteFavoriteItem(uri);
                break;
            case MOVIE_WITH_ID:
            default:
                throw new UnsupportedOperationException(
                        "Operation not implemented yet with uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private int deleteFavoriteItem(Uri uri) {
        String[] selectionArgs = new String[]{MovieContract.FavoriteEntry.getIdFromUri(uri)};
        return mOpenHelper.getWritableDatabase().delete(MovieContract.FavoriteEntry.TABLE_NAME,
                sFavoriteMovieIdSelection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIE:
                rowsUpdated = database.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case FAVORITES:
                rowsUpdated = database.update(MovieContract.FavoriteEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
//                rowsUpdated = updateMovieWithId(uri, values);
//                Log.v("WikiMovies", "SQLite updateMovieWithId 1 called");
//                break;
            default:
                throw new UnsupportedOperationException(
                        "Operation not implemented yet with uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

//    private int updateMovieWithId(Uri uri, ContentValues values) {
//        Log.v("WikiMovies", "SQLite updateMovieWithId 2 called");
//        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getIdFromUri(uri)};
//        return mOpenHelper.getReadableDatabase().update(MovieContract.MovieEntry.TABLE_NAME,
//                values, sIdSelection, selectionArgs);
//    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                database.beginTransaction();
                int retCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null,
                                value);
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
            case FAVORITES:
                database.beginTransaction();
                int retCountFav = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(MovieContract.FavoriteEntry.TABLE_NAME, null,
                                value);
                        if (_id != -1) {
                            retCountFav++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return retCountFav;
            case MOVIE_WITH_ID:
            default:
                throw new UnsupportedOperationException(
                        "Operation not implemented yet with uri: " + uri);
        }
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#",
                MOVIE_WITH_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES + "/#",
                FAVORITES_WITH_MOVIE_ID);

        // 3) Return the new matcher!
        return uriMatcher;
    }
}
