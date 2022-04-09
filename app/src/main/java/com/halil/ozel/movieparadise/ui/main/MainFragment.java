package com.halil.ozel.movieparadise.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import android.util.SparseArray;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.detail.DetailActivity;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.search.SearchActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class MainFragment extends BrowseFragment implements OnItemViewSelectedListener, OnItemViewClickedListener {

    @Inject
    TheMovieDbAPI theMovieDbAPI;

     GlideBackgroundManager glideBackgroundManager;

    // rows - 0 - now playing
    private static final int NOW_PLAYING = 0;

    // rows - 1 - top rated
    private static final int TOP_RATED = 1;

    // rows - 2 - popular
    private static final int POPULAR = 2;

    // rows - 3 - upcoming
    private static final int UPCOMING = 3;

    SparseArray<MovieRow> movieRowSparseArray;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.instance().appComponent().inject(this);

        glideBackgroundManager = new GlideBackgroundManager(getActivity());

        setBrandColor(ContextCompat.getColor(getActivity(), R.color.primary_transparent));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);


        // search icon background color
        setSearchAffordanceColor(getResources().getColor(R.color.black));


        // search icon clicked
        setOnSearchClickedListener(v -> {

            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);

        });


        createDataRows();
        createRows();
        prepareEntranceTransition();
        fetchNowPlayingMovies();
        fetchTopRatedMovies();
        fetchPopularMovies();
        fetchUpcomingMovies();
    }



    // Creates the data rows objects
    private void createDataRows() {

        movieRowSparseArray = new SparseArray<>();

        MoviePresenter moviePresenter = new MoviePresenter();

        //row - 0 - create objects
        movieRowSparseArray.put(NOW_PLAYING, new MovieRow()
                .setId(NOW_PLAYING)
                .setAdapter(new ArrayObjectAdapter(moviePresenter))
                .setTitle("Now Playing")
                .setPage(1)
        );

        //row - 1 - create objects
        movieRowSparseArray.put(TOP_RATED, new MovieRow()
                .setId(TOP_RATED)
                .setAdapter(new ArrayObjectAdapter(moviePresenter))
                .setTitle("Top Rated")
                .setPage(1)
        );

        //row - 2 - create objects
        movieRowSparseArray.put(POPULAR, new MovieRow()
                .setId(POPULAR)
                .setAdapter(new ArrayObjectAdapter(moviePresenter))
                .setTitle("Popular")
                .setPage(1)
        );

        //row - 3 - create objects
        movieRowSparseArray.put(UPCOMING, new MovieRow()
                .setId(UPCOMING)
                .setAdapter(new ArrayObjectAdapter(moviePresenter))
                .setTitle("Upcoming")
                .setPage(1)
        );
    }

    // Creates the rows and sets up the adapter of the fragment
    private void createRows() {
        // Creates the RowsAdapter for the Fragment
        // The ListRowPresenter tells to render ListRow objects
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (int i = 0; i < movieRowSparseArray.size(); i++) {
            MovieRow row = movieRowSparseArray.get(i);
            // Adds a new ListRow to the adapter. Each row will contain a collection of Movies
            // That will be rendered using the MoviePresenter
            HeaderItem headerItem = new HeaderItem(row.getId(), row.getTitle());
            ListRow listRow = new ListRow(headerItem, row.getAdapter());
            rowsAdapter.add(listRow);
        }
        // Sets this fragments Adapter.
        // The setAdapter method is defined in the BrowseFragment of the Leanback Library
        setAdapter(rowsAdapter);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    
    private void fetchNowPlayingMovies() {
        theMovieDbAPI.getNowPlayingMovies(Config.API_KEY_URL, movieRowSparseArray.get(NOW_PLAYING).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    bindMovieResponse(response, NOW_PLAYING);
                    startEntranceTransition();
                }, e -> {
                    Timber.e(e, "Error fetching now playing movies: %s", e.getMessage());
                });
    }


    private void fetchPopularMovies() {
        theMovieDbAPI.getPopularMovies(Config.API_KEY_URL, movieRowSparseArray.get(POPULAR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    bindMovieResponse(response, POPULAR);
                    startEntranceTransition();
                }, e -> {
                    Timber.e(e, "Error fetching popular movies: %s", e.getMessage());
                });
    }


    private void fetchUpcomingMovies() {
        theMovieDbAPI.getUpcomingMovies(Config.API_KEY_URL, movieRowSparseArray.get(UPCOMING).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    bindMovieResponse(response, UPCOMING);
                    startEntranceTransition();
                }, e -> Timber.e(e, "Error fetching upcoming movies: %s", e.getMessage()));
    }


    private void fetchTopRatedMovies() {
        theMovieDbAPI.getTopRatedMovies(Config.API_KEY_URL, movieRowSparseArray.get(TOP_RATED).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    bindMovieResponse(response, TOP_RATED);
                    startEntranceTransition();
                }, e -> Timber.e(e, "Error fetching top rated movies: %s", e.getMessage()));
    }


    private void bindMovieResponse(MovieResponse response, int id) {
        MovieRow movieRow = movieRowSparseArray.get(id);
        movieRow.setPage(movieRow.getPage() + 1);
        for(Movie movie : response.getResults()) {
            if (movie.getPosterPath() != null) { // Avoid showing movie without posters
                movieRow.getAdapter().add(movie);
            }
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        // Check if the item is a movie
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            // Check if the movie has a backdrop
            if(movie.getBackdropPath() != null) {
                glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + movie.getBackdropPath());
            } else {
                // If there is no backdrop for the movie we just use a default one
                glideBackgroundManager.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.material_bg));
            }
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            // Pass the movie to the activity
            intent.putExtra(Movie.class.getSimpleName(), movie);

            if (itemViewHolder.view instanceof MovieCardView) {
                // Pass the ImageView to allow a nice transition
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((MovieCardView) itemViewHolder.view).getPosterIV(),
                        DetailFragment.TRANSITION_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }
}
