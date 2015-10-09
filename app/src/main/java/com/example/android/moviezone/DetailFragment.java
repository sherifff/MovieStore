package com.example.android.moviezone;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviezone.data.MovieContract;
import com.squareup.picasso.Picasso;

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

public class DetailFragment extends Fragment {

    private static Movie movie;
    TrailerAdapter adapterTrailers;
    static final String DETAIL_MOVIE= "MOVIE";
    private ShareActionProvider mShareActionProvider;
    private TextView title;
    private TextView date;
    private TextView time;
    private TextView rate;
    private TextView  overview;
    private ImageView image;
    private NestedListView trailersView;
    public static ImageButton favourite;
    private TextView  review;
    static final String TITLE="title";
    static final String ID="id";
    static final String BACK_GROUND="back";
    static final String DATE="date";
    static final String TIME="time";
    static final String RATE="rate";
    static final String  OVERVIEW="overview";
    static final String IMAGE="image";
    private boolean pane;
//    static final String FAVOURITE="favourite";
    static boolean isfavourite;
    private static final String[] FAVOURITE_TRAILERS_COLUMNS={MovieContract.TrailersEntry.COLUMN_TRAILER_ID,MovieContract.TrailersEntry.COLUMN_KEY,MovieContract.TrailersEntry.COLUMN_NAME,MovieContract.TrailersEntry.COLUMN_SITE};
    private static final int COLUMN_TRAILER_ID = 0;
    private static final int  COLUMN_KEY= 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_SITE = 3;
    /////////////////////////////////////////////////////////////
    LoadMoreListView reviews;
    int totalPages;
    int page;
    ArrayList<Review> allReviews;
    ReviewsAdapter adapter;
    private static final String[] FAVOURITE_REVIEWS_COLUMNS={MovieContract.ReviewEntry.COLUMN_REVIEW_ID,MovieContract.ReviewEntry.COLUMN_AUTHOR,MovieContract.ReviewEntry.COLUMN_CONTENT,MovieContract.ReviewEntry.COLUMN_URL};
    private static final int COLUMN_REVIEWID = 0;
    private static final int  COLUMN_AUTHOR= 1;
    private static final int COLUMN_CONTENT = 2;
    private static final int COLUMN_URL = 3;
    //////////////////////////////////////////////////////////////////

    private static final String KEY_REVIEWS_LIST = "reviews";
    private static final String KEY_TRAILERS_LIST = "trailers";
    private static final String KEY_PAGES = "pages";
    private static final String VALID_PAGES = "valid";

    //////////////////////////////////////////////////////////////////

    public DetailFragment(){
        setHasOptionsMenu(true);
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void loadFromDatabase(){
        ArrayList<Trailer>elems=new ArrayList<>();
        Cursor cursor=getActivity().getContentResolver().query(MovieContract.TrailersEntry.buildTrailerForMovie(movie.getId()),FAVOURITE_TRAILERS_COLUMNS,null,null,null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Trailer elem=new Trailer(cursor.getString(COLUMN_KEY),cursor.getString(COLUMN_NAME),cursor.getString(COLUMN_SITE),cursor.getString(COLUMN_TRAILER_ID));
            elems.add(elem);
        }
        cursor.close();
        adapterTrailers.setTrailers(elems);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie =new Movie(arguments.getString(BACK_GROUND),arguments.getInt(ID),arguments.getString(TITLE),arguments.getString(IMAGE),arguments.getString(OVERVIEW),arguments.getString(DATE),arguments.getDouble(RATE));
        }
        pane=arguments.getBoolean("twoPane");
        isfavourite=false;
        adapterTrailers=new TrailerAdapter(getActivity(),new ArrayList<Trailer>());
        View view=inflater.inflate(R.layout.fragment_detail, container, false);
        title= (TextView) view.findViewById(R.id.detail_title);
        date=(TextView) view.findViewById(R.id.detail_date);
        rate=(TextView) view.findViewById(R.id.detail_rate);
        review=(TextView) view.findViewById(R.id.reviews);
        overview=(TextView) view.findViewById(R.id.detail_overview);
        image=(ImageView)view.findViewById(R.id.detail_image);
        favourite=(ImageButton)view.findViewById(R.id.favourite);
        trailersView=(NestedListView) view.findViewById(R.id.listview_trailers);
        if(movie.getOriginal_title()!=null&&movie.getOriginal_title().length()!=0&&!movie.getOriginal_title().equals("null")){
            title.setText(movie.getOriginal_title());
        }else{
            title.setText("Unknown Title..");
        }
        if(movie.getRelease_date()!=null&&movie.getRelease_date().length()!=0&&!movie.getRelease_date().equals("null")){
            date.setText(movie.getRelease_date());
        }else{
            date.setText("Unknown Release Date..");
        }
        String rate_string=""+movie.getVote_average();
        rate.setText(rate_string);

        if(movie.getOverview()!=null&&movie.getOverview().length()!=0&&!movie.getOverview().equals("null")){
            overview.setText(movie.getOverview());
        }else{
            overview.setText("No Overview..");
        }
        String path = "http://image.tmdb.org/t/p/w185"+movie.getPoster_path();
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        if(pane) {
            if (width < height) {
                Picasso.with(getActivity()).load(path).resize((int) (width * 0.3), (int) (width * 0.3 * 1.5)).into(image);
            } else {
                Picasso.with(getActivity()).load(path).resize((width*(2/15)), (int) (height / 2.3)).into(image);
            }
        }else{
            if (width < height) {
                Picasso.with(getActivity()).load(path).resize((int) (width / 2.3), (int) (height / 2.7)).into(image);
            } else {
                Picasso.with(getActivity()).load(path).resize((width/ 3), (int) (height / 1.5)).into(image);
            }
        }
        trailersView.setAdapter(adapterTrailers);
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildMovie(movie.getId()),FavouriteFragment.Favourite_COLUMNS,null,null,null);
        if(cursor!=null&&cursor.moveToFirst()) {
            favourite.setBackgroundResource(R.drawable.starg);
            isfavourite=true;
        }else {
            favourite.setBackgroundResource(R.drawable.stars2);
        }
        cursor.close();
        ////////////////////////////////////////////////////////////////////////////////
        allReviews= new ArrayList<Review>();
        totalPages=0;
        page=1;
        /////////////////////////////////////////////////////////////////////////////////

        if(savedInstanceState != null) {
            ArrayList<Trailer> data = savedInstanceState.getParcelableArrayList(KEY_TRAILERS_LIST);
            allReviews = savedInstanceState.getParcelableArrayList(KEY_REVIEWS_LIST);
            page=savedInstanceState.getInt(KEY_PAGES);
            totalPages=savedInstanceState.getInt(VALID_PAGES);
            if(data!=null){
                adapterTrailers.setTrailers(data);
            }
            if(data==null||data.size()==0){
                if(haveNetworkConnection()) {
                    new FetchTrailersTask().execute();
                }else if(isfavourite){
                    loadFromDatabase();
                }
            }
        } else {
            if(haveNetworkConnection()) {
                new FetchTrailersTask().execute();
            }else if(isfavourite){
                loadFromDatabase();
            }
        }
        trailersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                if (adapterTrailers.getElement(position).getKey() != null && adapterTrailers.getElement(position).getKey().length() != 0&&adapterTrailers.getElement(position).getSite().equalsIgnoreCase("YouTube")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v="+adapterTrailers.getElement(position).getKey()));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildMovie(movie.getId()),FavouriteFragment.Favourite_COLUMNS,null,null,null);
                if (cursor!=null&&cursor.moveToFirst()) {
                    isfavourite=false;
                    Toast.makeText(getActivity().getApplicationContext(), "Removed from Favourites :( ",
                            Toast.LENGTH_LONG).show();
                    getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movie.getId())});

                    getActivity().getContentResolver().delete(MovieContract.ReviewEntry.buildReviewForMovie(movie.getId()),
                            MovieContract.ReviewEntry.TABLE_NAME +
                                    "." +  MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movie.getId())});

                    getActivity().getContentResolver().delete(MovieContract.TrailersEntry.buildTrailerForMovie(movie.getId()),
                            MovieContract.TrailersEntry.TABLE_NAME +
                                    "." +  MovieContract.TrailersEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movie.getId())});
                    favourite.setBackgroundResource(R.drawable.stars2);

                } else {
                    ContentValues values=new ContentValues();
                    isfavourite=true;
                    Toast.makeText(getActivity().getApplicationContext(), "Added To Favourites ^_^ ",
                            Toast.LENGTH_LONG).show();
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getId());
                    values.put(MovieContract.MovieEntry.COLUMN_BACK_PATH,movie.getBackdrop_path());
                    values.put(MovieContract.MovieEntry.COLUMN_TITLE ,movie.getOriginal_title());
                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,movie.getPoster_path());
                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,movie.getOverview());
                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE,movie.getRelease_date());
                    values.put(MovieContract.MovieEntry.COLUMN_RATE,movie.getVote_average());
                    getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                    favourite.setBackgroundResource(R.drawable.starg);
                }
                cursor.close();
                GridMoviesFragment.updateAdapter();
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(getActivity(),ReviewsPopUp.class);
//                Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildMovie(movie.getId()),FavouriteFragment.Favourite_COLUMNS,null,null,null);
//                if (cursor!=null&&cursor.moveToFirst()) {
//                    intent.putExtra("favourite",true);
//                }else{
//                    intent.putExtra("favourite",false);
//                }
//                cursor.close();
//                intent.putExtra("movie_id",movie.getId());
//                startActivity(intent);

                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.reviews);
                allReviews=new ArrayList<Review>();
                adapter=new ReviewsAdapter(getActivity(),allReviews);
                reviews= (LoadMoreListView) dialog.findViewById(R.id.listview_reviews);
                reviews.setFooterDividersEnabled(true);
                reviews.setHeaderDividersEnabled(true);
                reviews.setAdapter(adapter);
                page=1;
                if(haveNetworkConnection()){
                        new FetchReviewsTask().execute(page);
                }else if(isfavourite){
                        loadRreviewsFromDatabase();
                }
                dialog.setCancelable(true);
                dialog.setTitle(getString(R.string.reviews));
                dialog.show();
                doKeepDialog(dialog);
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
//                reviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView adapterView, View view, int position, long l) {
//                        if (adapter != null){
//                            if (adapter.getElement(position).getUrl() != null && adapter.getElement(position).getUrl().length() != 0) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adapter.getElement(position).getUrl()));
//                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                                    startActivity(intent);
//                                }
//                            }
//                        }
//                    }
//                });
            }
        });
        return view;
    }

    public class FetchTrailersTask extends AsyncTask<Void, Void, ArrayList<Trailer>> {
        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private ArrayList<Trailer>getTrailersDataFromJson(String trailerJsonStr)
                throws JSONException {
            final String ID="id";
            final String NAME="name";
            final String KEY="key";
            final String SITE="site";
            final String RESULT="results";
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(RESULT);
            ArrayList<Trailer> list_trailers=new ArrayList<>();
            for (int i=0;i<trailerArray.length();i++){
                JSONObject obj= trailerArray.getJSONObject(i);
                Trailer trailer=new Trailer(obj.getString(KEY),obj.getString(NAME),obj.getString(SITE),obj.getString(ID));
                list_trailers.add(trailer);
            }
            return list_trailers;
        }
        @Override
        protected ArrayList<Trailer> doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailerJsonStr = null;

            try {

                final String TRAILER_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+movie.getId()+"/videos?";
                final String KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, getString(R.string.app_key))
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
                trailerJsonStr = buffer.toString();
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
                return getTrailersDataFromJson(trailerJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the Movies.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailer_array) {
            adapterTrailers.setTrailers(trailer_array);
            boolean notFound =true;
            boolean found =false;
            int j=0;
            String key="";
            while (notFound) {
                if (j < trailer_array.size()) {
                    if (trailer_array.get(j) != null && trailer_array.get(j).getKey().length() != 0 && trailer_array.get(j).getSite().equalsIgnoreCase("YouTube")) {
                        notFound = false;
                        found = true;
                        key = trailer_array.get(j).getKey();
                        j--;
                    }
                } else {
                    notFound = false;
                }
                j++;
            }
            if (j < adapterTrailers.getCount() && found&&mShareActionProvider!=null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent(key));
            }
            if(isfavourite) {
                for (int i = 0; i < trailer_array.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.TrailersEntry.COLUMN_MOVIE_ID, movie.getId());
                    values.put(MovieContract.TrailersEntry.COLUMN_TRAILER_ID, trailer_array.get(i).getId());
                    values.put(MovieContract.TrailersEntry.COLUMN_KEY, trailer_array.get(i).getKey());
                    values.put(MovieContract.TrailersEntry.COLUMN_NAME, trailer_array.get(i).getName());
                    values.put(MovieContract.TrailersEntry.COLUMN_SITE, trailer_array.get(i).getSite());
                    Cursor cursor = getActivity().getContentResolver().query(MovieContract.TrailersEntry.buildMovieIdTrailerId(movie.getId(), trailer_array.get(i).getId()),FAVOURITE_TRAILERS_COLUMNS,null,null,null);
                    if (cursor!=null&&cursor.moveToFirst()) {
                        getActivity().getContentResolver().update(MovieContract.TrailersEntry.buildMovieIdTrailerId(movie.getId(), trailer_array.get(i).getId()), values, null, null);
                    }else {
                        getActivity().getContentResolver().insert(MovieContract.TrailersEntry.buildMovieIdTrailerId(movie.getId(), trailer_array.get(i).getId()), values);
                    }
                    cursor.close();

                }
            }
        }

        @Override
        protected void onCancelled() {
            if(adapterTrailers.getCount()==0&&isfavourite){
                loadFromDatabase();
            }
        }
    }
    public void loadRreviewsFromDatabase(){
        ArrayList<Review>elems=new ArrayList<>();
        Cursor cursor=getActivity().getContentResolver().query(MovieContract.ReviewEntry.buildReviewForMovie(movie.getId()), FAVOURITE_REVIEWS_COLUMNS, null, null, null);
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
                        "http://api.themoviedb.org/3/movie/"+movie.getId()+"/reviews?";
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
            if(isfavourite) {
                for (int i = 0; i < reviews_array.size(); i++) {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie.getId());
                    values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviews_array.get(i).getId());
                    values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reviews_array.get(i).getAuthor());
                    values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reviews_array.get(i).getContent());
                    values.put(MovieContract.ReviewEntry.COLUMN_URL, reviews_array.get(i).getUrl());
                    Cursor cursor = getActivity().getContentResolver().query(MovieContract.ReviewEntry.buildMovieIdReviewId(movie.getId(), reviews_array.get(i).getId()), FAVOURITE_REVIEWS_COLUMNS, null, null, null);
                    if (cursor!=null&&cursor.moveToFirst()) {
                        getActivity().getContentResolver().update(MovieContract.ReviewEntry.buildMovieIdReviewId(movie.getId(), reviews_array.get(i).getId()), values, null, null);
                    }else {
                        getActivity().getContentResolver().insert(MovieContract.ReviewEntry.buildMovieIdReviewId(movie.getId(), reviews_array.get(i).getId()), values);
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
            if(adapter.getCount()==0&&isfavourite){
                loadRreviewsFromDatabase();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.

        boolean notFound =true;
        boolean found =false;
        int i=0;
        String key="";
            while (notFound) {
                if (i < adapterTrailers.getCount()) {
                    if (adapterTrailers.getElement(i) != null && adapterTrailers.getElement(i).getKey().length() != 0 && adapterTrailers.getElement(i).getSite().equalsIgnoreCase("YouTube")) {
                        notFound = false;
                        found = true;
                        key = adapterTrailers.getElement(i).getKey();
                        i--;
                    }
                } else {
                    notFound = false;
                }
                i++;
            }
            if (i < adapterTrailers.getCount() && found) {
                mShareActionProvider.setShareIntent(createShareForecastIntent(key));
            }
            //  mShareActionProvider.setShareIntent(createShareForecastIntent(adapterTrailers.getElement(0).getKey()));
    }
    private Intent createShareForecastIntent(String key) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v="+key);
        return shareIntent;
    }
    public static void setFavourite(int id){
        if(movie!=null&&movie.getId()==id) {
            favourite.setBackgroundResource(R.drawable.starg);
            isfavourite=true;
        }
    }
    public static void setNotFavourite(int id){
        if(movie!=null&&movie.getId()==id) {
            favourite.setBackgroundResource(R.drawable.stars2);
            isfavourite=false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(adapter !=null){
            outState.putParcelableArrayList(KEY_REVIEWS_LIST, adapter.getReviews());
        }
        if(adapterTrailers!=null){
            outState.putParcelableArrayList(KEY_TRAILERS_LIST,adapterTrailers.getTrailers());
        }
        outState.putInt(KEY_PAGES, page);
        outState.putInt(VALID_PAGES, totalPages);
        super.onSaveInstanceState(outState);

    }
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}