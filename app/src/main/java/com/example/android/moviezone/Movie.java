package com.example.android.moviezone;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String backdrop_path;
    private int id;
    private String original_title;
    private String poster_path;
    private String overview;
    private String release_date;
    private double vote_average;
    private static final String BACK_PATH = "backdrop_path";
    private static final String ID = "id";
    private static final String TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";


    public Movie(String backdrop_path, int id, String original_title, String poster_path, String overview, String release_date,
                 double vote_average) {
        this.backdrop_path=backdrop_path;
        this.id=id;
        this.original_title=original_title;
        this.poster_path=poster_path;
        this.overview=overview;
        this.release_date=release_date;
        this.vote_average=vote_average;
    }
    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString(BACK_PATH, backdrop_path);
        bundle.putInt(ID, id);
        bundle.putString(OVERVIEW, overview);
        bundle.putString(RELEASE_DATE, release_date);
        bundle.putString(POSTER_PATH, poster_path);
        bundle.putString(TITLE, original_title);
        bundle.putDouble(VOTE_AVERAGE, vote_average);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }
    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            return new Movie(bundle.getString(BACK_PATH),
                    bundle.getInt(ID),bundle.getString(TITLE),bundle.getString(POSTER_PATH),bundle.getString(OVERVIEW)
            ,bundle.getString(RELEASE_DATE),bundle.getDouble(VOTE_AVERAGE));
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };
}
