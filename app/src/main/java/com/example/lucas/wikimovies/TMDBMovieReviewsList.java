package com.example.lucas.wikimovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 9/22/2015.
 */
public class TMDBMovieReviewsList {
    public List<TMDBMovieReviewsItem> results;

    public class TMDBMovieReviewsItem {
        public String author;
        public String content;
        public TMDBMovieReviewsItem(String author, String content) {
            this.author = author;
            this.content = content;
        }
    }

    public TMDBMovieReviewsList(ArrayList<TMDBMovieReviewsItem> results) {
        this.results = results;
    }
}
