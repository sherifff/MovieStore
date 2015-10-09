package com.example.android.moviezone;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.moviezone.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReviewsPopUp extends Activity {
    LoadMoreListView reviews;
    int totalPages;
    int page;
    ArrayList<Review> allReviews;
    ReviewsAdapter adapter;
    int movie_id;
    boolean favourite=false;
    private static final String[] FAVOURITE_REVIEWS_COLUMNS={MovieContract.ReviewEntry.COLUMN_REVIEW_ID,MovieContract.ReviewEntry.COLUMN_AUTHOR,MovieContract.ReviewEntry.COLUMN_CONTENT,MovieContract.ReviewEntry.COLUMN_URL};
    private static final int COLUMN_REVIEWID = 0;
    private static final int  COLUMN_AUTHOR= 1;
    private static final int COLUMN_CONTENT = 2;
    private static final int COLUMN_URL = 3;
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews);

//        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.layout);
//        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.reviews, viewGroup);
//        reviews= (LoadMoreListView) layout.findViewById(R.id.listview_reviews);

        allReviews= new ArrayList<Review>();
        totalPages=0;
        page=1;
        movie_id=getIntent().getIntExtra("movie_id", 0);
        favourite=getIntent().getBooleanExtra("favourite", false);
        adapter=new ReviewsAdapter(this,allReviews);
        reviews= (LoadMoreListView) this.findViewById(R.id.listview_reviews);
        reviews.setFooterDividersEnabled(true);
        reviews.setHeaderDividersEnabled(true);
        reviews.setAdapter(adapter);
        if(haveNetworkConnection()){
            new FetchReviewsTask().execute(page);
        }else if(favourite){
            loadFromDatabase();
        }
        DisplayMetrics dis= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dis);
        int width=dis.widthPixels;
        int hi=dis.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (hi * .8));
        reviews.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            public void onLoadMore() {
                if (haveNetworkConnection()) {
                    if (page <= totalPages) {
                        new FetchReviewsTask().execute(page);
                    } else {
                        reviews.onLoadMoreComplete();
                    }
                }
            }
        });
        reviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                if(adapter.getElement(position).getUrl()!=null&&adapter.getElement(position).getUrl().length()!=0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adapter.getElement(position).getUrl()));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
    }
    public void loadFromDatabase(){
        ArrayList<Review>elems=new ArrayList<>();
        Cursor cursor=getContentResolver().query(MovieContract.ReviewEntry.buildReviewForMovie(movie_id),FAVOURITE_REVIEWS_COLUMNS,null,null,null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Review elem=new Review(cursor.getString(COLUMN_AUTHOR),cursor.getString(COLUMN_CONTENT),cursor.getString(COLUMN_URL),cursor.getString(COLUMN_REVIEWID));
            elems.add(elem);
        }
        cursor.close();
        adapter.setReviews(elems);
    }

    public class FetchReviewsTask extends AsyncTask<Integer, Void, ArrayList<Review>> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private ArrayList<Review>getReviewsDataFromJson(String reviewJsonStr)
                throws JSONException {
            final String ID="id";
            final String AUTHOR="author";
            final String CONTENT="content";
            final String URL="url";
            final String RESULT="results";
            final String TOTALPAGES="total_pages";
            JSONObject reviewsJson = new JSONObject(reviewJsonStr);
            totalPages= reviewsJson.getInt(TOTALPAGES);
            JSONArray reviewsArray = reviewsJson.getJSONArray(RESULT);
            ArrayList<Review> list_reviews=new ArrayList<>();
            for (int i=0;i<reviewsArray.length();i++){
                JSONObject obj= reviewsArray.getJSONObject(i);
                Review review=new Review(obj.getString(AUTHOR),obj.getString(CONTENT),obj.getString(URL),obj.getString(ID));
                list_reviews.add(review);
            }
            return list_reviews;
        }
        @Override
        protected ArrayList<Review> doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewJsonStr = null;



            try {

                final String REVIEW_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+movie_id+"/reviews?";
                final String PAGE_PARAM = "page";
                final String KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(REVIEW_BASE_URL).buildUpon()
                        .appendQueryParameter(PAGE_PARAM, params[0] + "")
                        .appendQueryParameter(KEY_PARAM,getString(R.string.app_key))
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getReviewsDataFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the Movies.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews_array) {
            adapter.setReviews(reviews_array);
            reviews.onLoadMoreComplete();
            if(favourite) {
                for (int i = 0; i < reviews_array.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie_id);
                    values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviews_array.get(i).getId());
                    values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reviews_array.get(i).getAuthor());
                    values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reviews_array.get(i).getContent());
                    values.put(MovieContract.ReviewEntry.COLUMN_URL, reviews_array.get(i).getUrl());
                    Cursor cursor = getContentResolver().query(MovieContract.ReviewEntry.buildMovieIdReviewId(movie_id,reviews_array.get(i).getId()),FAVOURITE_REVIEWS_COLUMNS,null,null,null);
                    if (cursor!=null&&cursor.moveToFirst()) {
                        getContentResolver().update(MovieContract.ReviewEntry.buildMovieIdReviewId(movie_id, reviews_array.get(i).getId()), values, null, null);
                    }else {
                        getContentResolver().insert(MovieContract.ReviewEntry.buildMovieIdReviewId(movie_id,reviews_array.get(i).getId()), values);
                    }
                    cursor.close();
                }
            }
            page++;
        }
        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            reviews.onLoadMoreComplete();
            if(adapter.getCount()==0&&favourite){
                loadFromDatabase();
            }
        }
    }
}