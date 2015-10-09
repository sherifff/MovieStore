package com.example.android.moviezone;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FavouriteAdapter extends CursorAdapter {
    private int width;
    private int height;
    private boolean pane;
    public FavouriteAdapter(Context context, Cursor c, int flags,boolean pane) {
        super(context, c, flags);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        this.pane=pane;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = R.layout.grid_item_favourite;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String poster_path = cursor.getString(FavouriteFragment.COLUMN_POSTER_PATH);
        String path = "http://image.tmdb.org/t/p/w185"+poster_path;
        if(pane) {
            if (width < height) {
                Picasso.with(context).load(path).resize( (width/5) , (int)((width/5)*1.5)).into(viewHolder.poster);
            } else {
                Picasso.with(context).load(path).resize((width*(2/15)), (width /5)).into(viewHolder.poster);
            }
        }else{
            if (width < height) {
                Picasso.with(context).load(path).resize(width / 2, (int) (height / 2.5)).into(viewHolder.poster);
            } else {
                Picasso.with(context).load(path).resize(width / 3, (int) (height / 1.3)).into(viewHolder.poster);
            }
        }
    }
    public static class ViewHolder {
        public final ImageView poster;
        public ViewHolder(View view) {
            poster = (ImageView) view.findViewById(R.id.item_image);
        }
    }
}