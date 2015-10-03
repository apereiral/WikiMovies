package com.example.lucas.wikimovies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lucas.wikimovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rafael on 23/08/2015.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_URI = "DETAIL_URI";
    private Uri mDetailUri;
    private static final int MOVIE_DETAIL_LOADER = 0;
    private String[] trailersKeysList;
    static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?";

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            mDetailUri = args.getParcelable(DETAIL_URI);
        }

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        if (mDetailUri == null) {
            return null;
        }

        return new CursorLoader(getActivity(), mDetailUri, Utility.getMovieTableColumns(),
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {return;}

        View view = getView();
        if (view == null) {
            return;
        }

        if (cursor.getString(Utility.COL_POSTER_PATH) != null) {
            Picasso.with(getActivity()).load(Utility.getPosterPathURL(cursor.getString(
                    Utility.COL_POSTER_PATH))).
                    into((ImageView) view.findViewById(R.id.selected_movie_poster));
        }
        if (cursor.getString(Utility.COL_ORIGINAL_TITLE) != null) {
            ((TextView)view.findViewById(R.id.original_title_text)).
                    setText(cursor.getString(Utility.COL_ORIGINAL_TITLE));
        }
        if (cursor.getString(Utility.COL_OVERVIEW) != null) {
            ((TextView)view.findViewById(R.id.overview_text)).
                    setText(cursor.getString(Utility.COL_OVERVIEW));
        }
        if (cursor.getString(Utility.COL_RELEASE_DATE) != null) {
            ((TextView)view.findViewById(R.id.release_date_text)).
                    setText(cursor.getString(Utility.COL_RELEASE_DATE));
        }
        ((TextView)view.findViewById(R.id.vote_average_text)).
                setText(cursor.getDouble(Utility.COL_VOTE_AVERAGE) + "/10");

        final int movieId = cursor.getInt(Utility.COL_MOVIE_ID);

        Cursor favoriteCursor = view.getContext().getContentResolver().
                query(MovieContract.FavoriteEntry.buildFavoriteWithIdUri(movieId),
                Utility.getMovieTableColumns(), null, null, null);

        boolean isFavorite = favoriteCursor.moveToFirst();

        final ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE,
                cursor.getString(Utility.COL_ORIGINAL_TITLE));
        values.put(MovieContract.FavoriteEntry.COLUMN_VOTE_AVERAGE,
                cursor.getDouble(Utility.COL_VOTE_AVERAGE));
        values.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                cursor.getString(Utility.COL_RELEASE_DATE));
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_PATH,
                cursor.getString(Utility.COL_POSTER_PATH));
        values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                cursor.getInt(Utility.COL_MOVIE_ID));
        values.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW,
                cursor.getString(Utility.COL_OVERVIEW));



        ImageButton imageButton = (ImageButton)view.findViewById(R.id.favorite_button);
        final TextView favoriteTextView = (TextView)view.findViewById(R.id.favorite_text);
        if (isFavorite){
            favoriteTextView.setText(R.string.unfavorite);
            imageButton.setSelected(true);
        } else {
            favoriteTextView.setText(R.string.favorite);
            imageButton.setSelected(false);
        }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    if (v.isSelected()) {
                        favoriteTextView.setText(R.string.unfavorite);
                        v.getContext().getContentResolver().insert(
                                MovieContract.FavoriteEntry.CONTENT_URI, values);
                    } else {
                        favoriteTextView.setText(R.string.favorite);
                        v.getContext().getContentResolver().delete(
                                MovieContract.FavoriteEntry.buildFavoriteWithIdUri(movieId), null,
                                null);
                    }

            }
        });

        getTrailers(cursor.getInt(Utility.COL_MOVIE_ID), view);
        getReviews(cursor.getInt(Utility.COL_MOVIE_ID), view);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void getTrailers(int movieId, final View view) {
        TMDBRestAdapter restAdapter = new TMDBRestAdapter();
        Callback<TMBDMovieTrailersList> cb = new Callback<TMBDMovieTrailersList>() {
            @Override
            public void success(TMBDMovieTrailersList tmbdMovieTrailersList, Response response) {
                Log.v("WikiMovies", "retrofit callback success 2: " + response.toString());
                trailersKeysList = new String[tmbdMovieTrailersList.results.size()];
                LinearLayout trailersList = (LinearLayout) view.findViewById(R.id.trailers_list);
                trailersList.removeAllViews();
                for (int i = 0; i < tmbdMovieTrailersList.results.size(); i++) {
                    if (tmbdMovieTrailersList.results.get(i).site.equalsIgnoreCase("youtube")) {
                        trailersKeysList[i] = tmbdMovieTrailersList.results.get(i).key;
                        final Uri uri =
                                Uri.parse(YOUTUBE_BASE_URL).buildUpon().
                                        appendQueryParameter("v", trailersKeysList[i]).build();
                        View trailerItem = View.inflate(view.getContext(), R.layout.list_item_trailer, null);
                        TextView trailerName = (TextView) trailerItem.findViewById(R.id.trailer_name);
                        trailerName.setText(tmbdMovieTrailersList.results.get(i).name);
                        trailersList.addView(trailerItem);
                        trailerItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
//                                if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
//                                    startActivity(intent);
//                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("WikiMovies", "retrofit callback error 2: " + error.toString());
            }
        };
        restAdapter.getTrailerJson(movieId, getActivity(), cb);
    }

    private void getReviews(int movieId, final View view) {
        TMDBRestAdapter restAdapter = new TMDBRestAdapter();
        Callback<TMDBMovieReviewsList> cb = new Callback<TMDBMovieReviewsList>() {
            @Override
            public void success(TMDBMovieReviewsList tmdbMovieReviewsList, Response response) {
                Log.v("WikiMovies", "retrofit callback success 3: " + response.toString());
                LinearLayout reviewsList = (LinearLayout) view.findViewById(R.id.reviews_list);
                reviewsList.removeAllViews();
                for (int i = 0; i < tmdbMovieReviewsList.results.size(); i++) {
                    String author = tmdbMovieReviewsList.results.get(i).author;
                    String content = tmdbMovieReviewsList.results.get(i).content;
                    View reviewItem = View.inflate(view.getContext(), R.layout.list_item_review, null);
                    TextView authorTV = (TextView) reviewItem.findViewById(R.id.author);
                    authorTV.setText(author);
                    TextView contentTV = (TextView) reviewItem.findViewById(R.id.content);
                    contentTV.setText(content);
                    reviewsList.addView(reviewItem);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("WikiMovies", "retrofit callback error 3: " + error.toString());
            }
        };
        restAdapter.getReviewsJson(movieId, getActivity(), cb);
    }
}
