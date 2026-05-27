package com.halil.ozel.movieparadise.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.search.SearchActivity;
import com.halil.ozel.movieparadise.ui.tv.TvDetailFragment;
import com.halil.ozel.movieparadise.ui.tv.TvShowCardView;
import com.halil.ozel.movieparadise.ui.tv.TvShowPresenter;

import java.util.List;

import javax.inject.Inject;

public class MainFragment extends BrowseSupportFragment
        implements OnItemViewSelectedListener, OnItemViewClickedListener, MainContract.View {

    private static final String TAG = "MainFragment";

    @Inject
    MainPresenter presenter;

    private GlideBackgroundManager glideBackgroundManager;
    private Object selectedItem;
    private SparseArray<MovieRow> movieRowSparseArray;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.instance().appComponent().inject(this);
        presenter.attachView(this);

        glideBackgroundManager = new GlideBackgroundManager(requireActivity());

        setBrandColor(ContextCompat.getColor(requireContext(), R.color.primary_dark_transparent));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setSearchAffordanceColor(ContextCompat.getColor(requireContext(), R.color.accent_color));

        setOnSearchClickedListener(v -> {
            startActivity(new Intent(requireActivity(), SearchActivity.class));
        });

        createDataRows();
        createRows();
        prepareEntranceTransition();

        presenter.loadHomeSections(rowId -> {
            MovieRow rowData = movieRowSparseArray.get(rowId);
            return rowData == null ? 1 : rowData.getPage();
        });
    }

    private void createDataRows() {
        movieRowSparseArray = new SparseArray<>();

        MoviePresenter moviePresenter = new MoviePresenter();
        TvShowPresenter tvPresenter   = new TvShowPresenter();

        movieRowSparseArray.put(MainContract.NOW_PLAYING,     newMovieRow(MainContract.NOW_PLAYING,     "Now Playing",   moviePresenter));
        movieRowSparseArray.put(MainContract.TOP_RATED,       newMovieRow(MainContract.TOP_RATED,       "Top Rated",     moviePresenter));
        movieRowSparseArray.put(MainContract.POPULAR,         newMovieRow(MainContract.POPULAR,         "Popular",       moviePresenter));
        movieRowSparseArray.put(MainContract.UPCOMING,        newMovieRow(MainContract.UPCOMING,        "Upcoming",      moviePresenter));
        movieRowSparseArray.put(MainContract.TV_ON_THE_AIR,   newMovieRow(MainContract.TV_ON_THE_AIR,   "On The Air",    tvPresenter));
        movieRowSparseArray.put(MainContract.TV_AIRING_TODAY, newMovieRow(MainContract.TV_AIRING_TODAY, "Airing Today",  tvPresenter));
        movieRowSparseArray.put(MainContract.TV_POPULAR,      newMovieRow(MainContract.TV_POPULAR,      "Popular TV",    tvPresenter));
        movieRowSparseArray.put(MainContract.TV_TOP_RATED,    newMovieRow(MainContract.TV_TOP_RATED,    "Top Rated TV",  tvPresenter));
    }

    private MovieRow newMovieRow(int id, String title, Presenter presenter) {
        return new MovieRow()
                .setId(id)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle(title)
                .setPage(1);
    }

    private void createRows() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
        for (int i = 0; i < movieRowSparseArray.size(); i++) {
            MovieRow row = movieRowSparseArray.get(i);
            HeaderItem header  = new HeaderItem(row.getId(), row.getTitle());
            rowsAdapter.add(new ListRow(header, row.getAdapter()));
        }
        setAdapter(rowsAdapter);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    private void addMoviesToRow(int rowId, List<Movie> movies) {
        MovieRow row = movieRowSparseArray.get(rowId);
        if (row == null || movies == null || movies.isEmpty()) {
            return;
        }
        row.setPage(row.getPage() + 1);
        for (Movie movie : movies) {
            if (movie.getPosterPath() != null) {
                row.getAdapter().add(movie);
            }
        }
    }

    private void addTvShowsToRow(int rowId, List<TvShow> shows) {
        MovieRow row = movieRowSparseArray.get(rowId);
        if (row == null || shows == null || shows.isEmpty()) {
            return;
        }
        row.setPage(row.getPage() + 1);
        for (TvShow show : shows) {
            if (show.getPosterPath() != null) {
                row.getAdapter().add(show);
            }
        }
    }

    @Override
    public void showMovieResults(int rowId, List<Movie> movies) {
        addMoviesToRow(rowId, movies);
        startEntranceTransition();
    }

    @Override
    public void showTvResults(int rowId, List<TvShow> shows) {
        addTvShowsToRow(rowId, shows);
        startEntranceTransition();
    }

    @Override
    public void showLoadError(String source, Throwable throwable) {
        Log.e(TAG, "load " + source + " error", throwable);
    }

    // ── Background ───────────────────────────────────────────────────────────

    private void updateBackground(Object item) {
        String path = null;
        if (item instanceof Movie) {
            path = ((Movie) item).getBackdropPath();
        } else if (item instanceof TvShow) {
            path = ((TvShow) item).getBackdropPath();
        }

        if (path != null) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + path);
        } else {
            glideBackgroundManager.setBackground(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.material_bg));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedItem != null) {
            updateBackground(selectedItem);
        }
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    // ── Listener callbacks ───────────────────────────────────────────────────

    @Override
    public void onItemSelected(Presenter.ViewHolder itemVH, Object item,
                               RowPresenter.ViewHolder rowVH, Row row) {
        selectedItem = item;
        updateBackground(item);
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

        } else if (item instanceof TvShow) {
            TvShow tvShow = (TvShow) item;
            Intent intent = MediaDetailActivity.newTvIntent(requireActivity(), tvShow);

            if (itemVH.view instanceof TvShowCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((TvShowCardView) itemVH.view).getPosterIV(),
                        TvDetailFragment.TRANSITION_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }
}
