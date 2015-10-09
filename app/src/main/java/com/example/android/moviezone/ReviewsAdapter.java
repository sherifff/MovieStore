package com.example.android.moviezone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewsAdapter extends BaseAdapter{

    private ArrayList<Review> reviews;
    private Context context;
    public ReviewsAdapter(Context context,ArrayList<Review> reviews){
        this.context=context;
        this.reviews=reviews;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void clear(){
        reviews=new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.review_elem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(reviews.get(position).getContent());
        holder.author.setText(reviews.get(position).getAuthor());
        return convertView;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    public void setReviews(ArrayList<Review>total_Reviews){
        if (total_Reviews != null) {
            for(Review review : total_Reviews) {
                reviews.add(review);
            }
        }
        notifyDataSetChanged();
    }
    public Review getElement(int position){
        return reviews.get(position) ;
    }

    public static class ViewHolder {
        public TextView author;
        public TextView content;
        public ViewHolder(View v){
            author = (TextView) v.findViewById(R.id.author);
            content = (TextView) v.findViewById(R.id.content);
        }
    }
}
