package com.example.android.moviezone;


public class Trailer {
    private String id;
   private String key;
    private String name;
    private String site;
    public Trailer(String key,String name, String site, String id){
        this.key=key;
        this.name=name;
        this.site=site;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
