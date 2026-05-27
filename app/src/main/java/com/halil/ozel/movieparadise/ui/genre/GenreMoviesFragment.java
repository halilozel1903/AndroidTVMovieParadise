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
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import java.util.List;

import javax.inject.Inject;

public class GenreMoviesFragment extends VerticalGridSupportFragment
        implements OnItemViewClickedListener, OnItemViewSelectedListener, GenreMoviesContract.View {

    private static final String TAG = "GenreMoviesFragment";
    private static final String ARG_GENRE = "arg_genre";
    private static final int GRID_COLUMNS = 5;
    private static final int LOAD_MORE_THRESHOLD = 8;

    @Inject
    GenreMoviesPresenter presenter;

    private Genre genre;
    private ArrayObjectAdapter moviesAdapter;

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
        presenter.attachView(this);

        if (getArguments() == null || !getArguments().containsKey(ARG_GENRE)) {
            throw new RuntimeException("A genre is necessary for GenreMoviesFragment");
        }
        genre = getArguments().getParcelable(ARG_GENRE);

        setTitle(genre.getName());
        setupGrid();
        presenter.loadMoviesByGenre(genre.getId());
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

    @Override
    public void showMovies(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return;
        }
        for (Movie movie : movies) {
            if (movie.getPosterPath() != null) {
                moviesAdapter.add(movie);
            }
        }
    }

    @Override
    public void showLoadError(Throwable throwable) {
        Log.e(TAG, "fetchMoviesByGenre error", throwable);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemVH, Object item,
                               RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie
                && moviesAdapter.indexOf(item) >= moviesAdapter.size() - LOAD_MORE_THRESHOLD
                && presenter.canLoadMore()) {
            presenter.loadMoviesByGenre(genre.getId());
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent intent = MediaDetailActivity.newMovieIntent(requireActivity(), movie);

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
        presenter.detachView();
        super.onDestroy();
    }
}
