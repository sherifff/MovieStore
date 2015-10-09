package com.example.android.moviezone;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.android.moviezone.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIE_FAVOURITES_LOADER = 0;
    private GridView grid;
    private FavouriteAdapter favouriteAdapter;
    boolean pane;

    public static final String[] Favourite_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_BACK_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE,
            MovieContract.MovieEntry.COLUMN_RATE
    };

    static final int _ID = 0;
    static final int COLUMN_POSTER_PATH = 1;
    static final int COLUMN_MOVIE_ID = 2;
    static final int COLUMN_BACK_PATH = 3;
    static final int COLUMN_TITLE = 4;
    static final int COLUMN_OVERVIEW = 5;
    static final int COLUMN_RELEASE = 6;
    static final int COLUMN_RATE = 7;
    public FavouriteFragment(){

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri favouriteMovies = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                favouriteMovies,
                Favourite_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favouriteAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            grid.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favouriteAdapter.swapCursor(null);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_FAVOURITES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        favouriteAdapter = new FavouriteAdapter(getActivity(), null, 0,getArguments().getBoolean("twoPane"));
        View rootView = inflater.inflate(R.layout.fragment_grid_movies, container, false);

        grid  = (GridView) rootView.findViewById(R.id.grid);
        ProgressBar bar= (ProgressBar) rootView.findViewById(R.id.loader_view);
        bar.setVisibility(View.INVISIBLE);
        grid.setAdapter(favouriteAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            grid.smoothScrollToPosition(mPosition);
        }
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                Cursor movie = (Cursor) parent.getItemAtPosition(position);
                ((CallBackFavourite)getActivity()).onItemSelectedFavourite(movie.getString(COLUMN_BACK_PATH),movie.getInt(COLUMN_MOVIE_ID)
                ,movie.getString(COLUMN_TITLE),movie.getString(COLUMN_POSTER_PATH),movie.getString(COLUMN_OVERVIEW),
                        movie.getString(COLUMN_RELEASE), movie.getDouble(COLUMN_RATE) );
                if(haveNetworkConnection()){
                  new FetchMovieIdTask().execute(movie.getInt(COLUMN_MOVIE_ID));
                }
            }
        });
        return rootView;

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);

    }


    public class FetchMovieIdTask extends AsyncTask<Integer, Void, Movie> {
        private final String LOG_TAG = FetchMovieIdTask.class.getSimpleName();

        private Movie getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String BACK_PATH="backdrop_path";
            final String ID="id";
            final String ORG_TITLE="original_title";
            final String POSTER_PATH="poster_path";
            final String OVERVIEW="overview";
            final String RELEASE_DATE="release_date";
            final String VOTE_AVERAGE="vote_average";
            final String RUN_TIME="runtime";
            JSONObject movieJson = new JSONObject(moviesJsonStr);
                Movie movie=new Movie(movieJson.getString(BACK_PATH),movieJson.getInt(ID),movieJson.getString(ORG_TITLE),movieJson.getString(POSTER_PATH),movieJson.getString(OVERVIEW),movieJson.getString(RELEASE_DATE),movieJson.getDouble(VOTE_AVERAGE));
            return movie;
        }
        @Override
        protected Movie doInBackground(Integer... params) {

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
                        "http://api.themoviedb.org/3/movie/"+params[0]+"?";
                final String KEY_PARAM = "api_key";
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
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
        protected void onPostExecute(Movie movie) {
            ContentValues values=new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_BACK_PATH,movie.getBackdrop_path());
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginal_title());
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getRelease_date());
            values.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getVote_average());
            getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, values,
                    MovieContract.MovieEntry.TABLE_NAME +
                            "." +  MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",new String[]{Integer.toString(movie.getId())});
        }

        @Override
        protected void onCancelled() {
              //startActivity(intent);
        }
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

    interface CallBackFavourite {
        public void onItemSelectedFavourite(String backdrop_path, int id,String original_title,String poster_path
                ,String overview,String release_date, double vote_average );
    }
}
