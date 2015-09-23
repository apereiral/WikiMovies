package com.example.lucas.wikimovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieArrayAdapter mMovieAdapter;
//    private List<MovieItemData> mTMDBData;
    private List<TMDBMovieItem> mTMDBMoviesListData;
    private List<String> posterList;
    public static final String SORT_METHOD = "SortMethod";
    final String BASE_URL = "http://api.themoviedb.org/3";
    final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    final String MY_API_KEY = "b9470b55903065a750eb1eb7bcf80538";
    final String POSTER_SIZE_PARAM = "w500";

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
//                intent.putExtra(Intent.EXTRA_TEXT, mTMDBData.get(i));
                intent.putExtra(Intent.EXTRA_TEXT, mTMDBMoviesListData.get(i));
                startActivity(intent);
            }
        });

        if (savedInstanceState == null || !savedInstanceState.containsKey("mTMDBData")) {
//            mTMDBData = new ArrayList<>();
            mTMDBMoviesListData = new ArrayList<>();
            SharedPreferences sortMethod = getActivity().getSharedPreferences(SORT_METHOD, 0);
//            FetchMoviesTask moviesTask = new FetchMoviesTask();
//            moviesTask.execute(sortMethod.getString("sort_method", "popularity.desc"));
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
            TMDBService service = restAdapter.create(TMDBService.class);
            service.getMovieList(sortMethod.getString("sort_method", "popularity.desc"),
                    MY_API_KEY, new Callback<TMDBMoviesList>() {
                        @Override
                        public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                            Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                            mTMDBMoviesListData = tmdbMoviesList.results;
                            mMovieAdapter.clear();
                            for (TMDBMovieItem item : mTMDBMoviesListData) {
//                                MovieItemData movieData = new MovieItemData(item.original_title,
//                                        item.vote_average, item.overview, item.release_date,
//                                        item.poster_path, item.id);
//                                mTMDBData.add(movieData);
                                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon().
                                    appendPath(POSTER_SIZE_PARAM).
                                    appendEncodedPath(item.poster_path).
                                    build();
                                mMovieAdapter.add(builtUri.toString());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("WikiMovies", "retrofit callback error 1: " + error.toString());
                        }
                    });

        } else {
            mTMDBMoviesListData = savedInstanceState.getParcelableArrayList("mTMDBMoviesListData");
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("poster_list", (ArrayList<String>) posterList);
//        outState.putParcelableArrayList("mTMDBData", (ArrayList<MovieItemData>) mTMDBData);
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
        SharedPreferences sortMethod = getActivity().getSharedPreferences(SORT_METHOD, 0);
        SharedPreferences.Editor editor = sortMethod.edit();
//        mTMDBData = new ArrayList<>();
//        FetchMoviesTask moviesTask = new FetchMoviesTask();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popular_movies) {
            editor.putString("sort_method", "popularity.desc");
            editor.commit();
//            moviesTask.execute("popularity.desc");
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
            TMDBService service = restAdapter.create(TMDBService.class);
            service.getMovieList("popularity.desc",
                    MY_API_KEY, new Callback<TMDBMoviesList>() {
                        @Override
                        public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                            Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                            mTMDBMoviesListData = tmdbMoviesList.results;
                            mMovieAdapter.clear();
                            for (TMDBMovieItem item : mTMDBMoviesListData) {
//                                MovieItemData movieData = new MovieItemData(item.original_title,
//                                        item.vote_average, item.overview, item.release_date,
//                                        item.poster_path, item.id);
//                                mTMDBData.add(movieData);
                                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon().
                                        appendPath(POSTER_SIZE_PARAM).
                                        appendEncodedPath(item.poster_path).
                                        build();
                                mMovieAdapter.add(builtUri.toString());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("WikiMovies", "retrofit callback error 1: " + error.toString());
                        }
                    });
            return true;
        }

        if (id == R.id.action_sort_highest_rate) {
            editor.putString("sort_method", "vote_average.desc");
            editor.commit();
//            moviesTask.execute("vote_average.desc");
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).build();
            TMDBService service = restAdapter.create(TMDBService.class);
            service.getMovieList("vote_average.desc",
                    MY_API_KEY, new Callback<TMDBMoviesList>() {
                        @Override
                        public void success(TMDBMoviesList tmdbMoviesList, Response response) {
                            Log.v("WikiMovies", "retrofit callback success 1: " + response.toString());
                            mTMDBMoviesListData = tmdbMoviesList.results;
                            mMovieAdapter.clear();
                            for (TMDBMovieItem item : mTMDBMoviesListData) {
//                                MovieItemData movieData = new MovieItemData(item.original_title,
//                                        item.vote_average, item.overview, item.release_date,
//                                        item.poster_path, item.id);
//                                mTMDBData.add(movieData);
                                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon().
                                        appendPath(POSTER_SIZE_PARAM).
                                        appendEncodedPath(item.poster_path).
                                        build();
                                mMovieAdapter.add(builtUri.toString());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("WikiMovies", "retrofit callback error 1: " + error.toString());
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
//
//        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            if (params.length == 0) {
//                return null;
//            }
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String movieListJsonStr = null;
//
//            try {
//
//                final String LIST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
//                final String API_KEY = "api_key";
//                final String SORT_BY_PARAM = "sort_by";
//                final String MY_API_KEY = "b9470b55903065a750eb1eb7bcf80538";
//
//                Uri builtUri = Uri.parse(LIST_BASE_URL).buildUpon().
//                        appendQueryParameter(SORT_BY_PARAM, params[0]).
//                        appendQueryParameter(API_KEY,MY_API_KEY).build();
//
//                URL url = new URL(builtUri.toString());
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                movieListJsonStr = buffer.toString();
//
////                Log.v(LOG_TAG, "Movie List JSON string: " + movieListJsonStr);
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try {
//                return getMovieListFromJson(movieListJsonStr);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, "JSON Error ", e);
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] strings) {
//
//            if (strings != null) {
//                mMovieAdapter.clear();
//                for (String moviePosterUrlStr : strings) {
//                    mMovieAdapter.add(moviePosterUrlStr);
//                }
//            }
//
//        }
//
//        private String[] getMovieListFromJson(String movieListJsonStr)
//                throws JSONException {
//
//            //The names of the JSON objects that need to be extracted.
//            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
//            final String POSTER_SIZE_PARAM = "w500";
//            final String POSTER_PATH_PARAM = "poster_path";
//            final String TMDB_RESULTS = "results";
//            final String TMDB_TITLE = "original_title";
//            final String TMDB_OVERVIEW = "overview";
//            final String TMDB_USER_RATING = "vote_average";
//            final String TMDB_RELEASE_DATE = "release_date";
//            final String TMDB_MOVIE_ID = "id";
//
//            JSONObject movieListJson = new JSONObject(movieListJsonStr);
//            JSONArray movieListArray = movieListJson.getJSONArray(TMDB_RESULTS);
//
//            String[] urlStrs = new String[movieListArray.length()];
//
//            for (int i = 0; i < movieListArray.length(); i++) {
//
//                JSONObject movieInList = movieListArray.getJSONObject(i);
//                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon().
//                        appendPath(POSTER_SIZE_PARAM).
//                        appendEncodedPath(movieInList.getString(POSTER_PATH_PARAM)).
//                        build();
//
//                MovieItemData movieItemData = new MovieItemData(movieInList.getString(TMDB_TITLE),
//                        movieInList.getInt(TMDB_USER_RATING),
//                        movieInList.getString(TMDB_OVERVIEW),
//                        movieInList.getString(TMDB_RELEASE_DATE),
//                        builtUri.toString(), movieInList.getString(TMDB_MOVIE_ID));
//
//                mTMDBData.add(movieItemData);
//                urlStrs[i] = movieItemData.posterPath;
//
//            }
//
//            return urlStrs;
//        }
//    }

}
