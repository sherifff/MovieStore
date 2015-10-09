package com.example.android.moviezone;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    private String id;
   private String key;
    private String name;
    private String site;
    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String SITE = "site";

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        bundle.putString(NAME, name);
        bundle.putString(KEY, key);
        bundle.putString(SITE, site);
        dest.writeBundle(bundle);
    }
    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {

        @Override
        public Trailer createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            return new Trailer(bundle.getString(KEY),bundle.getString(NAME),bundle.getString(SITE)
                    ,bundle.getString(ID));
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }

    };
}
