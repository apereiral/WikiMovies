package com.example.lucas.wikimovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 9/22/2015.
 */
public class TMDBMoviesList {

    public List<TMDBMovieItem> results;

    public TMDBMoviesList(ArrayList<TMDBMovieItem> results) {
        this.results = results;
    }
}
