package com.example.android.moviezone;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

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

public class GridMoviesFragment extends Fragment  {

    ArrayList<Movie> allMovies;
    ArrayList<Movie> Movies;
    int pages;
    int valid_pages;
    GridView grid;
    ProgressBar progress;
   // private int mPosition = GridView.INVALID_POSITION;
   // private static final String SELECTED_KEY = "selected_position";
    static GridAdapter adapter;
    // BOOLEAN TO CHECK IF NEW FEEDS ARE LOADING
    Boolean loadingMore = true;
    Boolean stopLoadingData = false;
    public GridMoviesFragment() {
        setHasOptionsMenu(true);
    }
    public void update(){
        adapter.clear();
        pages=1;
        new FetchMovieTask().execute(pages);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_grid_movies, container, false);
      pages=1;
        valid_pages=0;
        allMovies=new ArrayList<>();
        Movies=new ArrayList<>();
        grid= (GridView) view.findViewById(R.id.grid);
        progress= (ProgressBar) view.findViewById(R.id.loader_view);
        adapter=new GridAdapter(getActivity(), allMovies,getArguments().getBoolean("twoPane"));
        grid.setAdapter(adapter);

//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
//            grid.smoothScrollToPosition(mPosition);
//        }
        new FetchMovieTask().execute(pages);
        pages++;
        grid.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    if (stopLoadingData == false) {
                        // FETCH THE NEXT BATCH OF FEEDS
                        progress.setVisibility(View.VISIBLE);
                        new FetchMovieTask().execute(pages);
                        pages++;
                        if (pages > 1000 || pages > valid_pages) {
                            stopLoadingData = true;
                        }
                    }
                }
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = adapter.getElement(position);
                ((CallBack)getActivity()).onItemSelected(movie.getBackdrop_path(), movie.getId(), movie.getOriginal_title(),
                        movie.getPoster_path(), movie.getOverview(), movie.getRelease_date(), movie.getVote_average());
            }
        });

        return view;

    }
    public static void updateAdapter(){
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

//        if (mPosition != GridView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, mPosition);
//        }
        super.onSaveInstanceState(outState);
    }

    public class FetchMovieTask extends AsyncTask<Integer, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private ArrayList<Movie>getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String BACK_PATH="backdrop_path";
            final String ID="id";
            final String ORG_TITLE="original_title";
            final String POSTER_PATH="poster_path";
            final String OVERVIEW="overview";
            final String RELEASE_DATE="release_date";
            final String VOTE_AVERAGE="vote_average";
            final String RESULT="results";
            final String TOTALPAGES="total_pages";

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            valid_pages= movieJson.getInt(TOTALPAGES);
            JSONArray moviesArray = movieJson.getJSONArray(RESULT);
            ArrayList<Movie> list_movies=new ArrayList<>();
            for (int i=0;i<moviesArray.length();i++){
              JSONObject obj= moviesArray.getJSONObject(i);
                Movie movie=new Movie(obj.getString(BACK_PATH),obj.getInt(ID),obj.getString(ORG_TITLE),obj.getString(POSTER_PATH),obj.getString(OVERVIEW),obj.getString(RELEASE_DATE),obj.getDouble(VOTE_AVERAGE));
                list_movies.add(movie);
            }
            return list_movies;
        }
        @Override
        protected ArrayList<Movie> doInBackground(Integer... params) {
            // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
            // MORE DATA WHILE LOADING A BATCH
            loadingMore = true;

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;



            try {

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String PAGE_PARAM = "page";
                final String KEY_PARAM = "api_key";
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM,prefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_most_popular)))
                        .appendQueryParameter(PAGE_PARAM, params[0] + "")
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
                movieJsonStr = buffer.toString();
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
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the Movies.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            // CHANGE THE LOADING MORE STATUS
            loadingMore = false;
//            if (movies != null) {
//                for(Movie movie : movies) {
//                    allMovies.add(movie);
//                }
//            }
            adapter.setMovies(movies);
            progress.setVisibility(View.GONE);
        }
    }
    interface CallBack {
        public void onItemSelected(String backdrop_path, int id,String original_title,String poster_path
                ,String overview,String release_date, double vote_average );
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
//        if (mPosition != GridView.INVALID_POSITION) {
//            grid.smoothScrollToPosition(mPosition);
//        }
    }
}