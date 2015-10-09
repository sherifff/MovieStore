package com.example.android.moviezone;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TrailerAdapter extends BaseAdapter{

    private ArrayList<Trailer> trailers;
    private Context context;
    public TrailerAdapter(Context context,ArrayList<Trailer> trailers){
        this.context=context;
        this.trailers=trailers;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void clear(){
        trailers=new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trailer_elem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(trailers.get(position).getName());
        return convertView;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }
    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    public void setTrailers(ArrayList<Trailer>total_trailers){
        if (total_trailers != null) {
            for(Trailer trailer : total_trailers) {
                trailers.add(trailer);
            }
        }
        notifyDataSetChanged();
    }
    public Trailer getElement(int position){
        return trailers.get(position) ;
    }

    public  static class ViewHolder {
        public TextView name;
        public ViewHolder(View v){
           name = (TextView) v.findViewById(R.id.trailer_name);
        }
    }
}