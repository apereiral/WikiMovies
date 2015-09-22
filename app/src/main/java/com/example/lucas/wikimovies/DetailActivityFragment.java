package com.example.lucas.wikimovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private MovieItemData movieItemData;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieItemData = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            Picasso.with(getActivity()).load(movieItemData.posterPath).
                    into((ImageView) rootView.findViewById(R.id.selected_movie_poster));
            ((TextView)rootView.findViewById(R.id.original_title_text)).
                    setText(movieItemData.originalTitle);
            ((TextView)rootView.findViewById(R.id.overview_text)).
                    setText(movieItemData.overview);
            ((TextView)rootView.findViewById(R.id.release_date_text)).
                    setText(movieItemData.releaseDate);
            ((TextView)rootView.findViewById(R.id.vote_average_text)).
                    setText(movieItemData.voteAverage + "/10");
//            if (((ImageButton)rootView.findViewById(R.id.favorite_button)).isSelected()) {
//                ((TextView)rootView.findViewById(R.id.favorite_text)).setText(R.string.favorite);
//            } else {
//                ((TextView)rootView.findViewById(R.id.favorite_text)).setText(R.string.unfavorite);
//            }
            ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.favorite_button);
            final TextView favoriteTextView = (TextView)rootView.findViewById(R.id.favorite_text);
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

        return rootView;
    }
}
