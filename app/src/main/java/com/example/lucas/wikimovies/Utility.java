package com.example.lucas.wikimovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.lucas.wikimovies.data.MovieContract;

/**
 * Created by Lucas on 9/23/2015.
 */
public class Utility {

    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE_PARAM = "w342";
    static final String SORT_METHOD = "SortMethod";

    static final String SORT_MOST_POPULAR = "popularity.desc";
    static final String SORT_HIGHEST_RATE = "vote_average.desc";
    static final String SORT_FAVORITES = "favorites";

    private static final String[] MOVIE_TABLE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_ORIGINAL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_RELEASE_DATE = 5;
    static final int COL_VOTE_AVERAGE = 6;


    public static String getPosterPathURL(String posterPath) {
        return Uri.parse(POSTER_BASE_URL).buildUpon().
                appendPath(POSTER_SIZE_PARAM).
                appendEncodedPath(posterPath).
                build().toString();
    }

    public static String getPreferredSortMethod(Context context) {
        SharedPreferences sortMethod = context.getSharedPreferences(SORT_METHOD, 0);
        return sortMethod.getString("sort_method", "popularity.desc");
    }

    public static void setPreferredSortMethod(Context context, String sortMethodStr) {
        SharedPreferences sortMethod = context.getSharedPreferences(SORT_METHOD, 0);
        SharedPreferences.Editor editor = sortMethod.edit();
        editor.putString("sort_method", sortMethodStr);
        editor.commit();
    }

    public static String[] getMovieTableColumns() {
        return MOVIE_TABLE_COLUMNS;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
