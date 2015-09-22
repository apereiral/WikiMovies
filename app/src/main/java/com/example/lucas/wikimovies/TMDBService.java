package com.example.lucas.wikimovies;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Lucas on 9/21/2015.
 */
public interface TMDBService {
    @GET("/discover/movie")
    Call<TMDBMoviesList> getMovieList(@Query("sort_by") String sort_by, @Query("api_key") String api_key);

    @GET("/movie/{id}/videos")
    Call<TMBDMovieTrailersList> getMovieTrailers(@Path("id") int id, @Query("api_key") String api_key);

    @GET("/movie/{id}/reviews")
    Call<TMDBMovieReviewsList> getMovieReviews(@Path("id") int id, @Query("api_key") String api_key);
}
