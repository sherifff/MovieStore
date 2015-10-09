package com.example.android.moviezone;

public class Movie {
    private String backdrop_path;
    private int id;
    private String original_title;
    private String poster_path;
    private String overview;
    private String release_date;
    private double vote_average;

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
}
