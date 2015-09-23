package com.example.lucas.wikimovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Created by Lucas on 9/23/2015.
 */
public class Utility {

    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE_PARAM = "w500";
    static final String SORT_METHOD = "SortMethod";


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

}
