package com.example.lucas.wikimovies;

import android.content.Context;

import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by Lucas on 9/23/2015.
 */
public class TMDBRestAdapter {

    static final String BASE_URL = "http://api.themoviedb.org/3";
    static final String MY_API_KEY = "b9470b55903065a750eb1eb7bcf80538";

    protected RestAdapter restAdapter;
    protected TMDBService service;

    public TMDBRestAdapter() {
        restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
        service = restAdapter.create(TMDBService.class);
    }

    public void getMovieList(Context context, Callback<TMDBMoviesList> cb) {
        service.getMovieList(Utility.getPreferredSortMethod(context), MY_API_KEY, cb);
    }

    public void getTrailerJson(int movieId, Context context, Callback<TMBDMovieTrailersList> cb) {
        service.getMovieTrailers(movieId, MY_API_KEY, cb);
    }

    public void getReviewsJson(int movieId, Context context, Callback<TMDBMovieReviewsList> cb) {
        service.getMovieReviews(movieId, MY_API_KEY, cb);
    }

}
