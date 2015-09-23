package com.example.lucas.wikimovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rafael on 23/08/2015.
 */
public class MovieItemData implements Parcelable {
    String originalTitle;
    double voteAverage;
    String overview;
    String releaseDate;
    String posterPath;
    int movieId;

    public MovieItemData(String originalTitle, double voteAverage, String overview,
                         String releaseDate, String posterPath, int movieId) {
        this.originalTitle = originalTitle;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.movieId = movieId;
    }

    protected MovieItemData(Parcel in) {
        originalTitle = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        movieId = in.readInt();
    }

    public static final Creator<MovieItemData> CREATOR = new Creator<MovieItemData>() {
        @Override
        public MovieItemData createFromParcel(Parcel in) {
            return new MovieItemData(in);
        }

        @Override
        public MovieItemData[] newArray(int size) {
            return new MovieItemData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeInt(movieId);
    }
}
