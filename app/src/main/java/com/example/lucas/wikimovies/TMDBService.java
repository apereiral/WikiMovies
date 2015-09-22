package com.example.lucas.wikimovies;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Lucas on 9/21/2015.
 */
public interface TMDBService {
    @GET("/discover/movie")
    void getMovieList(@Query("sort_by") String sort_by, @Query("api_key") String api_key, Callback<TMDBMoviesList> callback);

    @GET("/movie/{id}/videos")
    void getMovieTrailers(@Path("id") int id, @Query("api_key") String api_key, Callback<TMBDMovieTrailersList> callback);

    @GET("/movie/{id}/reviews")
    void getMovieReviews(@Path("id") int id, @Query("api_key") String api_key, Callback<TMDBMovieReviewsList> callback);
}
