package com.example.android.moviezone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.moviezone.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter{

   private ArrayList<Movie>movies;
   private Context context;
    private int width;
    private int height;
    private boolean pane;
    private static final String[] Favourite_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };
    public GridAdapter(Context context,ArrayList<Movie>movies,boolean pane){
        this.context=context;
        this.movies=movies;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
         width = displayMetrics.widthPixels;
         height = displayMetrics.heightPixels;
        this.pane=pane;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void clear(){
        movies=new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.buildMovie(movies.get(position).getId()),Favourite_COLUMNS,null,null,null);
        if (cursor!=null&&cursor.moveToFirst()) {
            holder.favourite.setBackgroundResource(R.drawable.starg);

        } else {
            holder.favourite.setBackgroundResource(R.drawable.stars);
        }
        cursor.close();
        final int finalPosition = position;
        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn = (ImageButton) v.findViewById(R.id.favourite);
                Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.buildMovie(movies.get(finalPosition).getId()), Favourite_COLUMNS, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    Toast.makeText(context.getApplicationContext(), "Removed from Favourites :( ",
                            Toast.LENGTH_LONG).show();
                    context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.TABLE_NAME +
                                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movies.get(finalPosition).getId())});
                    btn.setBackgroundResource(R.drawable.stars);

                    context.getContentResolver().delete(MovieContract.ReviewEntry.buildReviewForMovie(movies.get(finalPosition).getId()),
                            MovieContract.ReviewEntry.TABLE_NAME +
                                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movies.get(finalPosition).getId())});
                    context.getContentResolver().delete(MovieContract.TrailersEntry.buildTrailerForMovie(movies.get(finalPosition).getId()),
                            MovieContract.TrailersEntry.TABLE_NAME +
                                    "." + MovieContract.TrailersEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{Integer.toString(movies.get(finalPosition).getId())});
                    DetailFragment.setNotFavourite(movies.get(finalPosition).getId());

                } else {
                    btn.setBackgroundResource(R.drawable.starg);
                    Toast.makeText(context.getApplicationContext(), "Added To Favourites ^_^",
                            Toast.LENGTH_LONG).show();
                    DetailFragment.setFavourite(movies.get(finalPosition).getId());
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movies.get(finalPosition).getId());
                    values.put(MovieContract.MovieEntry.COLUMN_BACK_PATH, movies.get(finalPosition).getBackdrop_path());
                    values.put(MovieContract.MovieEntry.COLUMN_TITLE, movies.get(finalPosition).getOriginal_title());
                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movies.get(finalPosition).getPoster_path());
                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movies.get(finalPosition).getOverview());
                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE, movies.get(finalPosition).getRelease_date());
                    values.put(MovieContract.MovieEntry.COLUMN_RATE, movies.get(finalPosition).getVote_average());
                    context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                    //context.getContentResolver().notifyAll();
                }
                cursor.close();
            }
        });
        String path = "http://image.tmdb.org/t/p/w185"+movies.get(position).getPoster_path();
        if(pane) {
            if (width < height) {
                Picasso.with(context).load(path).resize( (width/5) , (int)((width/5)*1.5)).into(holder.image);
            } else {
                Picasso.with(context).load(path).resize((width*(2/15)), (width /5)).into(holder.image);
            }
        }else{
            if (width < height) {
                Picasso.with(context).load(path).resize(width / 2, (int) (height / 2.5)).into(holder.image);
            } else {
                Picasso.with(context).load(path).resize(width / 3, (int) (height / 1.3)).into(holder.image);
            }
        }
        holder.favourite.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    public void setMovies(ArrayList<Movie>total_movies){
        if (total_movies != null) {
                for(Movie movie : total_movies) {
                    movies.add(movie);
                }
        }
        notifyDataSetChanged();
    }
    public ArrayList<Movie> getMovies(){
        return  movies;
    }
    public Movie getElement(int position){
        return movies.get(position) ;
    }

    public static class ViewHolder {
        public ImageView image;
        public ImageButton favourite;
        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.item_image);
            favourite=(ImageButton) view.findViewById(R.id.favourite);
        }
    }

}
