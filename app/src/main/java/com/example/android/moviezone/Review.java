package com.example.android.moviezone;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String id;
    private String author;
    private String content;
    private String url;
    private static final String ID = "id";
    private static final String AUTHER = "author";
    private static final String CONTENT = "content";
    private static final String URL = "url";
    public Review(String author, String content, String url, String id){
        this.author=author;
        this.content=content;
        this.url=url;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        bundle.putString(AUTHER, author);
        bundle.putString(CONTENT, content);
        bundle.putString(URL, url);
        dest.writeBundle(bundle);

    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            return new Review(bundle.getString(AUTHER),bundle.getString(CONTENT),bundle.getString(URL)
                    ,bundle.getString(ID));
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }

    };
}
