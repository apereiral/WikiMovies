package com.example.lucas.wikimovies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Rafael on 23/08/2015.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final int MOVIE_DETAIL_LOADER = 0;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(), intent.getData(), Utility.getMovieTableColumns(),
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) {return;}

        View view = getView();

        Picasso.with(getActivity()).load(Utility.getPosterPathURL(cursor.getString(
                Utility.COL_POSTER_PATH))).
                into((ImageView) view.findViewById(R.id.selected_movie_poster));
        ((TextView)view.findViewById(R.id.original_title_text)).
                setText(cursor.getString(Utility.COL_ORIGINAL_TITLE));
        ((TextView)view.findViewById(R.id.overview_text)).
                setText(cursor.getString(Utility.COL_OVERVIEW));
        ((TextView)view.findViewById(R.id.release_date_text)).
                setText(cursor.getString(Utility.COL_RELEASE_DATE));
        ((TextView)view.findViewById(R.id.vote_average_text)).
                setText(cursor.getDouble(Utility.COL_VOTE_AVERAGE) + "/10");

        ImageButton imageButton = (ImageButton)view.findViewById(R.id.favorite_button);
        final TextView favoriteTextView = (TextView)view.findViewById(R.id.favorite_text);
        if (imageButton.isSelected()){
            favoriteTextView.setText(R.string.unfavorite);
        } else {
            favoriteTextView.setText(R.string.favorite);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    favoriteTextView.setText(R.string.unfavorite);
                } else {
                    favoriteTextView.setText(R.string.favorite);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
