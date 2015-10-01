package com.example.lucas.wikimovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.lucas.wikimovies.data.MovieContract;

import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private MovieCursorAdapter mMovieAdapter;
    private static final int MOVIE_LOADER = 0;

    public interface DetailCallback {
        public void onItemSelected (Uri uri);
    }

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_movies);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    ((DetailCallback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getInt(Utility.COL_ID)));
//                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
//                    intent.setData(
//                            MovieContract.MovieEntry.buildMovieUri(cursor.getInt(Utility.COL_ID)));
//                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Utility.getPreferredSortMethod(getActivity()).equals(Utility.SORT_FAVORITES)) {
            displayFavoritesList();
        } else {
            updateMovieList();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popular_movies) {
            Utility.setPreferredSortMethod(getActivity(), Utility.SORT_MOST_POPULAR);

            updateMovieList();

            return true;
        }

        if (id == R.id.action_sort_highest_rate) {
            Utility.setPreferredSortMethod(getActivity(), Utility.SORT_HIGHEST_RATE);

            updateMovieList();

            return true;
        }

        if (id == R.id.action_sort_favorites) {
            Utility.setPreferredSortMethod(getActivity(), Utility.SORT_FAVORITES);

            displayFavoritesList();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI,
                Utility.getMovieTableColumns(), null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    private void updateMovieList() {
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                null, null);
        TMDBRestAdapter restAdapter = new TMDBRestAdapter();
        Callback<TMDBMoviesList> cb = new Callback<TMDBMoviesList>() {
            @Override
            public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                Vector<ContentValues> contentValuesVector = new Vector<ContentValues>();
                for (TMDBMovieItem item : tmdbMoviesList.results) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, item.id);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, item.original_title);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, item.overview);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, item.poster_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, item.release_date);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, item.vote_average);

                    contentValuesVector.add(movieValues);
                }
                if (contentValuesVector.size() > 0) {
                    ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                    contentValuesVector.toArray(contentValuesArray);
                    getActivity().getContentResolver().bulkInsert(
                            MovieContract.MovieEntry.CONTENT_URI, contentValuesArray);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("WikiMovies", "retrofit callback error 1: " + error.toString());
            }
        };
        restAdapter.getMovieList(getActivity(), cb);
    }

    private void displayFavoritesList() {
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                null, null);
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI, Utility.getMovieTableColumns(), null,
                null, null);
        if (!cursor.moveToFirst()) {
            return;
        }
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>();
        do {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, cursor.getInt(Utility.COL_MOVIE_ID));
            values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, cursor.getString(Utility.COL_ORIGINAL_TITLE));
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, cursor.getString(Utility.COL_OVERVIEW));
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, cursor.getString(Utility.COL_POSTER_PATH));
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, cursor.getString(Utility.COL_RELEASE_DATE));
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, cursor.getDouble(Utility.COL_VOTE_AVERAGE));

            contentValuesVector.add(values);
        }while(cursor.moveToNext());

        ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
        contentValuesVector.toArray(contentValuesArray);
        getActivity().getContentResolver().bulkInsert(
                MovieContract.MovieEntry.CONTENT_URI, contentValuesArray);
    }
}
