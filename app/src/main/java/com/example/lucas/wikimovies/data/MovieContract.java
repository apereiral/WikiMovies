package com.example.lucas.wikimovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lucas on 9/19/2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.lucas.wikimovies.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
//    public static final String PATH_TMDB_MOVIE_ID = "tmdb_movie_id";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // Columns
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
//        public static final String CONLUMN_TMDB_MOVIE_ID_KEY = "tmdb_movie_id_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILERS_JSON_OBJECT = "trailers_json_object";
        public static final String COLUMN_REVIEWS_JSON_OBJECT = "reviews_json_object";

        public static Uri buildMovieUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

//        public static Uri buildPosterUri (String posterPath) {
//            return CONTENT_URI.buildUpon().
//                    appendPath(posterPath).build();
//        }

//        public static String getPosterPathFromUri (Uri uri) {
//            return uri.getPathSegments().get(1);
//        }

    }

//    public static final class TMDBMovieIdEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
//                appendPath(PATH_TMDB_MOVIE_ID).build();
//
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
//                CONTENT_AUTHORITY + "/" + PATH_TMDB_MOVIE_ID;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
//                CONTENT_AUTHORITY + "/" + PATH_TMDB_MOVIE_ID;
//
//        // Table name
//        public static final String TABLE_NAME = "tmdb_movie_id";
//
//        // Columns
//        public static final String COLUMN_MOVIE_ID = "movie_id";
//        public static final String COLUMN_TRAILERS_JSON_OBJECT = "trailers_json_object";
//        public static final String COLUMN_REVIEWS_JSON_OBJECT = "reviews_json_object";
//
//    }
}
