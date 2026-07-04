package com.halil.ozel.movieparadise.ui.genre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.common.RowLoadingHelper;
import com.halil.ozel.movieparadise.ui.base.TvRows;
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
    private static final int GRID_SKELETON_COUNT = 10;

    @Inject
    GenreMoviesPresenter presenter;

    private Genre genre;
    private ArrayObjectAdapter moviesAdapter;
    private final RowLoadingHelper rowLoadingHelper = new RowLoadingHelper();

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
        VerticalGridPresenter gridPresenter = TvRows.verticalGridPresenter();
        gridPresenter.setNumberOfColumns(GRID_COLUMNS);
        setGridPresenter(gridPresenter);

        moviesAdapter = new ArrayObjectAdapter(
                rowLoadingHelper.createSelector(new MoviePresenter(), Movie.class));
        setAdapter(moviesAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    @Override
    public void onLoadStarted(boolean isFirstPage) {
        if (isFirstPage) {
            rowLoadingHelper.showInitialLoading(moviesAdapter, GRID_SKELETON_COUNT);
        } else {
            rowLoadingHelper.showPaginationLoading(moviesAdapter, RowLoadingHelper.PAGINATION_SKELETON_COUNT);
        }
    }

    @Override
    public void showMovies(List<Movie> movies) {
        rowLoadingHelper.clearLoading(moviesAdapter);
        rowLoadingHelper.clearState(moviesAdapter);
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
    public void showEmpty() {
        rowLoadingHelper.clearLoading(moviesAdapter);
        rowLoadingHelper.showEmpty(moviesAdapter, getString(R.string.empty_genre));
    }

    @Override
    public void showLoadError(Throwable throwable) {
        rowLoadingHelper.showError(
                moviesAdapter,
                getString(R.string.error_network),
                () -> presenter.retry(genre.getId()));
        Log.e(TAG, "fetchMoviesByGenre error", throwable);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemVH, Object item,
                               RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie
                && moviesAdapter.indexOf(item) >= rowLoadingHelper.getContentSize(moviesAdapter) - LOAD_MORE_THRESHOLD
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
