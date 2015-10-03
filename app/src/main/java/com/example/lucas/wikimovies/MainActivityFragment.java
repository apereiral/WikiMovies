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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private int mPosition;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TextView mEmptyListView;
    private static final String POSITION_KEY = "position_key";
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

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);

        mEmptyListView = (TextView)rootView.findViewById(R.id.empty_list_view);

        mMovieAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.grid_movies);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                mPosition = i;
                if (cursor != null) {
                    ((DetailCallback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getInt(Utility.COL_ID)));
                }
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        if (savedInstanceState == null) {
            Utility.setPreferredSortMethod(getActivity(), Utility.SORT_MOST_POPULAR);
            updateMovieList();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.getPreferredSortMethod(getActivity()).equals(Utility.SORT_FAVORITES)) {
            displayFavoritesList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(POSITION_KEY, mPosition);
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
        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
        updateEmptyView(!data.moveToFirst());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    private void updateMovieList() {
//        mEmptyListView.setText("");
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                null, null);
        TMDBRestAdapter restAdapter = new TMDBRestAdapter();
        Callback<TMDBMoviesList> cb = new Callback<TMDBMoviesList>() {
            @Override
            public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                mProgressBar.setVisibility(View.INVISIBLE);
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
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.e("WikiMovies", "retrofit callback error 1: " + error.toString());
                CharSequence text = "Check your network connection.";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getActivity(), text, duration);
                toast.show();
            }
        };
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyListView.setText("");
        restAdapter.getMovieList(getActivity(), cb);
    }

    private void displayFavoritesList() {
//        mEmptyListView.setText("");
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                null, null);
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI, Utility.getMovieTableColumns(), null,
                null, null);
        if (cursor == null || !cursor.moveToFirst()) {
//            mEmptyListView.setText(R.string.empty_favorites_list);
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
        cursor.close();
//        updateEmptyView();
    }

    private void updateEmptyView(boolean isEmptyList) {
        if (!isEmptyList) {
            mEmptyListView.setVisibility(View.INVISIBLE);
            return;
        }

        mEmptyListView.setVisibility(View.VISIBLE);

        if (Utility.getPreferredSortMethod(getActivity()) != Utility.SORT_FAVORITES) {
            mEmptyListView.setText(R.string.no_connection);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mEmptyListView.setText(R.string.empty_favorites_list);
        }
    }
}
