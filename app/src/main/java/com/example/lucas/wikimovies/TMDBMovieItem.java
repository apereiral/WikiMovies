package com.example.lucas.wikimovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lucas on 9/23/2015.
 */
public class TMDBMovieItem implements Parcelable {

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

    protected TMDBMovieItem(Parcel in) {
        backdrop_path = in.readString();
        id = in.readInt();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
    }

    public static final Creator<TMDBMovieItem> CREATOR = new Creator<TMDBMovieItem>() {
        @Override
        public TMDBMovieItem createFromParcel(Parcel in) {
            return new TMDBMovieItem(in);
        }

        @Override
        public TMDBMovieItem[] newArray(int size) {
            return new TMDBMovieItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(backdrop_path);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(vote_average);
    }
}
