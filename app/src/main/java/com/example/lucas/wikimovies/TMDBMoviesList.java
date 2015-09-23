package com.example.lucas.wikimovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 9/22/2015.
 */
public class TMDBMoviesList {
    public class TMDBMovieItem {
        public String backdrop_path;
        public int id;
        public String original_title;
        public String overview;
        public String release_date;
        public String poster_path;
        public double vote_average;
        public TMDBMovieItem(String backdrop_path, int id, String original_title, String overview,
                         String release_date, String poster_path, double vote_average){
            this.backdrop_path = backdrop_path;
            this.id = id;
            this.original_title = original_title;
            this.overview = overview;
            this.release_date = release_date;
            this.poster_path = poster_path;
            this.vote_average = vote_average;
        }
    }

    public List<TMDBMovieItem> results;

    public TMDBMoviesList(ArrayList<TMDBMovieItem> results) {
        this.results = results;
    }
}
