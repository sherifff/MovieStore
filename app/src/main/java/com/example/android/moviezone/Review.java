package com.example.android.moviezone;

public class Review {
    private String id;
    private String author;
    private String content;
    private String url;
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
}
