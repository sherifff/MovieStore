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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE_FAVOURITE = 100;
    static final int MOVIE_FAVOURITE_id = 101;
    static final int MOVIE_REVIEWS = 102;
    static final int MOVIE_TRAILERS = 103;
    static final int MOVIE_REVIEWS_TOTAL = 104;
    static final int MOVIE_TRAILERS_TOTAL = 105;
//    private static final SQLiteQueryBuilder sMovieQueryBuilder;
//
//    static {
//        sMovieQueryBuilder = new SQLiteQueryBuilder();
//        sMovieQueryBuilder.setTables(
//                MovieContract.MovieEntry.TABLE_NAME);
//    }


    private static final String sMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." +  MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String reviewsSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." +  MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND "
                    +  MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ";

    private static final String reviewsSelectionTotal =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." +  MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String trailersSelection =
            MovieContract.TrailersEntry.TABLE_NAME +
                    "." +  MovieContract.TrailersEntry.COLUMN_MOVIE_ID + " = ? AND "
                    +  MovieContract.TrailersEntry.COLUMN_TRAILER_ID + " = ? ";

    private static final String trailersSelectionTotal =
            MovieContract.TrailersEntry.TABLE_NAME +
                    "." +  MovieContract.TrailersEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovie(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs=new String[]{MovieContract.MovieEntry.getMovieId(uri)};
        String selection=sMovieSelection;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
        );
    }
    private Cursor getReview(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs=new String[]{MovieContract.ReviewEntry.getMovieId(uri),MovieContract.ReviewEntry.getReviewId(uri)};
        String selection=reviewsSelection;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
        );
    }
    private Cursor getReviews(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs = new String[]{MovieContract.ReviewEntry.getMovieId(uri)};
        ;
        String selection = reviewsSelectionTotal;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
        );
    }
    private Cursor getTrailer(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs=new String[]{MovieContract.TrailersEntry.getMovieId(uri),MovieContract.TrailersEntry.getTrailerId(uri)};
        String selection=trailersSelection;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailersEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
        );
    }
    private int UpdateReview(Uri uri,ContentValues values){
        String[] selectionArgs=new String[]{MovieContract.ReviewEntry.getMovieId(uri),MovieContract.ReviewEntry.getReviewId(uri)};
        String selection=reviewsSelection;
       return mOpenHelper.getReadableDatabase().update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
               selectionArgs);
    }
    private int UpdateTrailer(Uri uri,ContentValues values){
        String[] selectionArgs=new String[]{MovieContract.TrailersEntry.getMovieId(uri),MovieContract.TrailersEntry.getTrailerId(uri)};
        String selection=trailersSelection;
        return mOpenHelper.getReadableDatabase().update(MovieContract.TrailersEntry.TABLE_NAME, values, selection,
                selectionArgs);
    }
    private Cursor getTrailers(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs=new String[]{MovieContract.TrailersEntry.getMovieId(uri)};
        String selection=trailersSelectionTotal;

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailersEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
        );
    }
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE_MOVIES+ "/#",MOVIE_FAVOURITE_id);
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE_MOVIES,MOVIE_FAVOURITE);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS+ "/#/*",MOVIE_REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS+ "/#/*",MOVIE_TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS+ "/#",MOVIE_REVIEWS_TOTAL);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS+ "/#",MOVIE_TRAILERS_TOTAL);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_FAVOURITE_id:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_FAVOURITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case MOVIE_TRAILERS:
                return MovieContract.TrailersEntry.CONTENT_ITEM_TYPE;
            case MOVIE_TRAILERS_TOTAL:
                return MovieContract.TrailersEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS_TOTAL:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_FAVOURITE_id: {
                retCursor = getMovie( uri,projection,sortOrder);
                break;
            }
            case MOVIE_REVIEWS: {
                retCursor = getReview(uri, projection, sortOrder);
                break;
            }
            case MOVIE_REVIEWS_TOTAL: {
                retCursor = getReviews(uri, projection, sortOrder);
                break;
            }
            case MOVIE_TRAILERS_TOTAL: {
                retCursor = getTrailers(uri, projection, sortOrder);
                break;
            }
            case MOVIE_TRAILERS: {
                retCursor = getTrailer(uri, projection, sortOrder);
                break;
            }
            case MOVIE_FAVOURITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_FAVOURITE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildFavouriteMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_TRAILERS: {
                long _id = db.insert(MovieContract.TrailersEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailersEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE_FAVOURITE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_REVIEWS:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_TRAILERS:
                rowsDeleted = db.delete(
                        MovieContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_REVIEWS_TOTAL:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_TRAILERS_TOTAL:
                rowsDeleted = db.delete(
                        MovieContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE_FAVOURITE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_REVIEWS:
                rowsUpdated = UpdateReview(uri,values);
                break;
            case MOVIE_TRAILERS:
                rowsUpdated = UpdateTrailer(uri,values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE_FAVOURITE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOVIE_REVIEWS_TOTAL:
                db.beginTransaction();
                 returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case MOVIE_TRAILERS_TOTAL:
                db.beginTransaction();
                 returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}