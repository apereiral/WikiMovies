package com.example.lucas.wikimovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieArrayAdapter mMovieAdapter;
    private List<TMDBMovieItem> mTMDBMoviesListData;
    private List<String> posterList;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState == null || !savedInstanceState.containsKey("poster_list")) {
            posterList = new ArrayList<>();
        } else {
            posterList = savedInstanceState.getStringArrayList("poster_list");
        }

        mMovieAdapter = new MovieArrayAdapter(getActivity(),
                R.layout.grid_item_movie,
                posterList);

        final GridView gridView = (GridView) rootView.findViewById(R.id.grid_movies);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mTMDBMoviesListData.get(i));
                startActivity(intent);
            }
        });

        if (savedInstanceState == null || !savedInstanceState.containsKey("mTMDBData")) {

            mTMDBMoviesListData = new ArrayList<>();

            TMDBRestAdapter restAdapter = new TMDBRestAdapter();
            Callback<TMDBMoviesList> cb = new Callback<TMDBMoviesList>() {
                @Override
                public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                    Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                    mTMDBMoviesListData = tmdbMoviesList.results;
                    mMovieAdapter.clear();
                    for (TMDBMovieItem item : mTMDBMoviesListData) {
                        mMovieAdapter.add(Utility.getPosterPathURL(item.poster_path));
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
                    mMovieAdapter.clear();
                    for (TMDBMovieItem item : mTMDBMoviesListData) {
                        mMovieAdapter.add(Utility.getPosterPathURL(item.poster_path));
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
                    mMovieAdapter.clear();
                    for (TMDBMovieItem item : mTMDBMoviesListData) {
                        mMovieAdapter.add(Utility.getPosterPathURL(item.poster_path));
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
