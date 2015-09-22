package com.example.lucas.wikimovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 9/22/2015.
 */
public class TMBDMovieTrailersList {
    public List<TMBDMovieTrailersItem> results;

    public class TMBDMovieTrailersItem {
        public String key;
        public String name;
        public String site;
        public TMBDMovieTrailersItem(String key, String name, String site) {
            this.key = key;
            this.name = name;
            this.site = site;
        }
    }

    public TMBDMovieTrailersList(ArrayList<TMBDMovieTrailersItem> results) {
        this.results = results;
    }
}
