package com.halil.ozel.movieparadise.ui.genre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.ui.detail.DetailActivity;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GenreMoviesFragment extends VerticalGridSupportFragment
        implements OnItemViewClickedListener, OnItemViewSelectedListener {

    private static final String TAG = "GenreMoviesFragment";
    private static final String ARG_GENRE = "arg_genre";
    private static final int GRID_COLUMNS = 5;
    private static final int LOAD_MORE_THRESHOLD = 8;

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private Genre genre;
    private ArrayObjectAdapter moviesAdapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int page = 1;
    private int totalPages = 1;
    private boolean loading;

    public static GenreMoviesFragment newInstance(Genre genre) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_GENRE, genre);
        GenreMoviesFragment fragment = new GenreMoviesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);

        if (getArguments() == null || !getArguments().containsKey(ARG_GENRE)) {
            throw new RuntimeException("A genre is necessary for GenreMoviesFragment");
        }
        genre = getArguments().getParcelable(ARG_GENRE);

        setTitle(genre.getName());
        setupGrid();
        fetchMoviesByGenre();
    }

    private void setupGrid() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL);
        gridPresenter.setNumberOfColumns(GRID_COLUMNS);
        setGridPresenter(gridPresenter);

        moviesAdapter = new ArrayObjectAdapter(new MoviePresenter());
        setAdapter(moviesAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    private void fetchMoviesByGenre() {
        if (loading || page > totalPages) {
            return;
        }

        loading = true;
        disposables.add(theMovieDbAPI.getMoviesByGenre(
                        genre.getId(),
                        "popularity.desc",
                        false,
                        page,
                        Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieResponse, e -> {
                    loading = false;
                    Log.e(TAG, "fetchMoviesByGenre error", e);
                }));
    }

    private void bindMovieResponse(MovieResponse response) {
        loading = false;
        if (response == null || response.getResults() == null) {
            return;
        }

        totalPages = Math.max(1, response.getTotalPages());
        page = response.getPage() + 1;
        for (Movie movie : response.getResults()) {
            if (movie.getPosterPath() != null) {
                moviesAdapter.add(movie);
            }
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemVH, Object item,
                               RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie
                && moviesAdapter.indexOf(item) >= moviesAdapter.size() - LOAD_MORE_THRESHOLD) {
            fetchMoviesByGenre();
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent intent = new Intent(requireActivity(), DetailActivity.class);
            intent.putExtra(Movie.class.getSimpleName(), movie);

            if (itemVH.view instanceof MovieCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((MovieCardView) itemVH.view).getPosterIV(),
                        DetailFragment.TRANSITION_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
