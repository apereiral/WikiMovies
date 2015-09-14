package com.example.lucas.wikimovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lucas on 8/16/2015.
 */
public class MovieArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mPosterList;
    private int mResource;

    public MovieArrayAdapter(Context context, int resource, List<String> posterList) {
        super(context, resource, posterList);

        mContext = context;
        mResource = resource;
        mPosterList = posterList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(mResource, parent, false);

        }

        String posterUrl = mPosterList.get(position);
        Picasso.with(mContext).load(posterUrl).into((ImageView)view.findViewById(R.id.grid_item_movie));
        return view;

    }

}
