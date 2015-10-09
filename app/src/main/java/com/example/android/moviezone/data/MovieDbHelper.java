/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.moviezone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviezone.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_BACK_PATH + " TEXT, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH+ " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT," +
                MovieEntry.COLUMN_RELEASE + " TEXT," +
                MovieEntry.COLUMN_RATE + " REAL, " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID+ ") ON CONFLICT REPLACE);";



        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT," +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT, " +
                MovieContract.ReviewEntry.COLUMN_URL + " TEXT, " +
                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + MovieContract.ReviewEntry.COLUMN_REVIEW_ID+ ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MovieContract.TrailersEntry.TABLE_NAME + " (" +
                MovieContract.TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.TrailersEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieContract.TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL," +
                MovieContract.TrailersEntry.COLUMN_KEY + " TEXT NOT NULL," +
                MovieContract.TrailersEntry.COLUMN_NAME + " TEXT, " +
                MovieContract.TrailersEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.TrailersEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +
                " UNIQUE (" + MovieContract.TrailersEntry.COLUMN_TRAILER_ID+ ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailersEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}