package com.example.lucas.wikimovies;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.lucas.wikimovies.data.MovieContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

//    private MovieArrayAdapter mMovieAdapter;
    private MovieCursorAdapter mMovieAdapter;
    private List<TMDBMovieItem> mTMDBMoviesListData;
    private List<String> posterList;

    private static final String[] MOVIE_TABLE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_TRAILERS_JSON_OBJECT,
            MovieContract.MovieEntry.COLUMN_REVIEWS_JSON_OBJECT
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_ORIGINAL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_RELEASE_DATE = 5;
    static final int COL_VOTE_AVERAGE = 6;
    static final int COL_TRAILERS_JSON = 7;
    static final int COL_REVIEWS_JSON = 8;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        if (savedInstanceState == null || !savedInstanceState.containsKey("poster_list")) {
//            posterList = new ArrayList<>();
//        } else {
//            posterList = savedInstanceState.getStringArrayList("poster_list");
//        }

//        mMovieAdapter = new MovieArrayAdapter(getActivity(),
//                R.layout.grid_item_movie,
//                posterList);
        
        mMovieAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        final GridView gridView = (GridView) rootView.findViewById(R.id.grid_movies);
        gridView.setAdapter(mMovieAdapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(view.getContext(), DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, mTMDBMoviesListData.get(i));
//                startActivity(intent);
//            }
//        });

        if (savedInstanceState == null || !savedInstanceState.containsKey("mTMDBMoviesListData")) {

            mTMDBMoviesListData = new ArrayList<>();

            TMDBRestAdapter restAdapter = new TMDBRestAdapter();
            Callback<TMDBMoviesList> cb = new Callback<TMDBMoviesList>() {
                @Override
                public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                    Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                    mTMDBMoviesListData = tmdbMoviesList.results;
//                    mMovieAdapter.clear();
                    Vector<ContentValues> contentValuesVector = new Vector<ContentValues>();
                    for (TMDBMovieItem item : mTMDBMoviesListData) {
//                        mMovieAdapter.add(Utility.getPosterPathURL(item.poster_path));
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
        } else {
            mTMDBMoviesListData = savedInstanceState.getParcelableArrayList("mTMDBMoviesListData");
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("poster_list", (ArrayList<String>) posterList);
        outState.putParcelableArrayList("mTMDBMoviesListData", (ArrayList<TMDBMovieItem>) mTMDBMoviesListData);
        super.onSaveInstanceState(outState);
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
            Utility.setPreferredSortMethod(getActivity(), "popularity.desc");

            TMDBRestAdapter restAdapter = new TMDBRestAdapter();
            Callback<TMDBMoviesList> cb = new Callback<TMDBMoviesList>() {
                @Override
                public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                    Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                    mTMDBMoviesListData = tmdbMoviesList.results;
//                    mMovieAdapter.clear();
                    Vector<ContentValues> contentValuesVector = new Vector<ContentValues>();
                    for (TMDBMovieItem item : mTMDBMoviesListData) {
//                        mMovieAdapter.add(Utility.getPosterPathURL(item.poster_path));
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

            return true;
        }

        if (id == R.id.action_sort_highest_rate) {
            Utility.setPreferredSortMethod(getActivity(), "vote_average.desc");

            TMDBRestAdapter restAdapter = new TMDBRestAdapter();
            Callback<TMDBMoviesList> cb = new Callback<TMDBMoviesList>() {
                @Override
                public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                    Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                    mTMDBMoviesListData = tmdbMoviesList.results;
//                    mMovieAdapter.clear();
                    Vector<ContentValues> contentValuesVector = new Vector<ContentValues>();
                    for (TMDBMovieItem item : mTMDBMoviesListData) {
//                        mMovieAdapter.add(Utility.getPosterPathURL(item.poster_path));
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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
