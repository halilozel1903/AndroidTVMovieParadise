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
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.base.TvRows;
import com.halil.ozel.movieparadise.ui.common.RowLoadingHelper;
import com.halil.ozel.movieparadise.ui.common.UiStateItem;
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
    private static final String STATE_SELECTED_ROW = "state_selected_row";

    @Inject
    MainPresenter presenter;

    private GlideBackgroundManager glideBackgroundManager;
    private Object selectedItem;
    private SparseArray<MovieRow> movieRowSparseArray;
    private SparseArray<ListRow> visibleRows;
    private SparseArray<RowLoadingHelper> rowLoadingHelpers;
    private ArrayObjectAdapter rowsAdapter;
    private boolean entranceTransitionStarted;
    private int pendingRestoreRow = -1;

    private interface RowItemBinder<T> {
        String key(T item);
        boolean canShow(T item);
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            pendingRestoreRow = savedInstanceState.getInt(STATE_SELECTED_ROW, -1);
        }
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

        loadInitialRows();
    }

    private void createDataRows() {
        movieRowSparseArray = new SparseArray<>();
        visibleRows = new SparseArray<>();
        rowLoadingHelpers = new SparseArray<>();

        MoviePresenter moviePresenter = new MoviePresenter();
        TvShowPresenter tvPresenter = new TvShowPresenter();

        movieRowSparseArray.put(MainContract.NOW_PLAYING,
                newMovieRow(MainContract.NOW_PLAYING, "Now Playing", moviePresenter, Movie.class));
        movieRowSparseArray.put(MainContract.TOP_RATED,
                newMovieRow(MainContract.TOP_RATED, "Top Rated", moviePresenter, Movie.class));
        movieRowSparseArray.put(MainContract.POPULAR,
                newMovieRow(MainContract.POPULAR, "Popular", moviePresenter, Movie.class));
        movieRowSparseArray.put(MainContract.UPCOMING,
                newMovieRow(MainContract.UPCOMING, "Upcoming", moviePresenter, Movie.class));
        movieRowSparseArray.put(MainContract.TV_ON_THE_AIR,
                newMovieRow(MainContract.TV_ON_THE_AIR, "On The Air", tvPresenter, TvShow.class));
        movieRowSparseArray.put(MainContract.TV_AIRING_TODAY,
                newMovieRow(MainContract.TV_AIRING_TODAY, "Airing Today", tvPresenter, TvShow.class));
        movieRowSparseArray.put(MainContract.TV_POPULAR,
                newMovieRow(MainContract.TV_POPULAR, "Popular TV", tvPresenter, TvShow.class));
        movieRowSparseArray.put(MainContract.TV_TOP_RATED,
                newMovieRow(MainContract.TV_TOP_RATED, "Top Rated TV", tvPresenter, TvShow.class));
    }

    private MovieRow newMovieRow(int id, String title, Presenter contentPresenter, Class<?> contentClass) {
        RowLoadingHelper helper = new RowLoadingHelper();
        rowLoadingHelpers.put(id, helper);
        return new MovieRow()
                .setId(id)
                .setAdapter(new ArrayObjectAdapter(helper.createSelector(contentPresenter, contentClass)))
                .setTitle(title)
                .setPage(1);
    }

    private RowLoadingHelper helperFor(int rowId) {
        return rowLoadingHelpers.get(rowId);
    }

    private void createRows() {
        rowsAdapter = new ArrayObjectAdapter(TvRows.listRowPresenter());
        setAdapter(rowsAdapter);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    private void loadInitialRows() {
        for (int i = 0; i < movieRowSparseArray.size(); i++) {
            loadNextPage(movieRowSparseArray.keyAt(i));
        }
    }

    private void scheduleLoadNextPage(int rowId) {
        View root = getView();
        if (root == null) {
            return;
        }
        root.post(() -> {
            if (isAdded()) {
                loadNextPage(rowId);
            }
        });
    }

    private void loadNextPage(int rowId) {
        MovieRow row = movieRowSparseArray.get(rowId);
        if (row == null || row.isLoading() || row.isExhausted()) {
            return;
        }
        RowLoadingHelper helper = helperFor(rowId);
        if (helper == null) {
            return;
        }

        row.setLoading(true);
        row.setError(false);

        if (!row.isHasShownContent()) {
            addVisibleRowIfNeeded(row);
            helper.showInitialLoading(row.getAdapter(), RowLoadingHelper.INITIAL_SKELETON_COUNT);
        } else {
            helper.showPaginationLoading(row.getAdapter(), RowLoadingHelper.PAGINATION_SKELETON_COUNT);
        }

        presenter.loadHomeSection(rowId, row.getPage());
    }

    private <T> int addItemsToRow(int rowId, List<T> items, RowItemBinder<T> binder) {
        MovieRow row = movieRowSparseArray.get(rowId);
        RowLoadingHelper helper = helperFor(rowId);
        if (row == null || helper == null || items == null || items.isEmpty()) {
            return 0;
        }
        helper.clearLoading(row.getAdapter());
        helper.clearState(row.getAdapter());

        int oldSize = helper.getContentSize(row.getAdapter());
        for (T item : items) {
            if (binder.canShow(item) && row.addKeyIfAbsent(binder.key(item))) {
                row.getAdapter().add(item);
            }
        }
        int addedCount = helper.getContentSize(row.getAdapter()) - oldSize;
        if (addedCount > 0) {
            row.setHasShownContent(true);
            addVisibleRowIfNeeded(row);
        }
        return addedCount;
    }

    private String movieKey(Movie movie) {
        if (movie == null) {
            return null;
        }
        if (movie.getId() != null) {
            return "movie:" + movie.getId();
        }
        return "movie:" + movie.getTitle() + ":" + movie.getReleaseDate();
    }

    private String tvShowKey(TvShow show) {
        if (show == null) {
            return null;
        }
        if (show.getId() != null) {
            return "tv:" + show.getId();
        }
        return "tv:" + show.getName() + ":" + show.getFirstAirDate();
    }

    private void addVisibleRowIfNeeded(MovieRow row) {
        if (visibleRows.get(row.getId()) != null) {
            return;
        }
        ListRow listRow = new ListRow(new HeaderItem(row.getId(), row.getTitle()), row.getAdapter());
        visibleRows.put(row.getId(), listRow);
        rowsAdapter.add(visibleRowIndex(row.getId()), listRow);
    }

    private void removeVisibleRow(int rowId) {
        ListRow listRow = visibleRows.get(rowId);
        if (listRow == null) {
            return;
        }
        int index = rowsAdapter.indexOf(listRow);
        if (index >= 0) {
            rowsAdapter.removeItems(index, 1);
        }
        visibleRows.remove(rowId);
    }

    private int visibleRowIndex(int rowId) {
        int index = 0;
        for (int i = 0; i < visibleRows.size(); i++) {
            if (visibleRows.keyAt(i) < rowId) {
                index++;
            }
        }
        return index;
    }

    @Override
    public void showMovieResponse(int rowId, MovieResponse response) {
        List<Movie> movies = response == null ? null : response.getResults();
        int responsePage = response == null ? currentPage(rowId) : response.getPage();
        int totalPages = response == null ? responsePage : response.getTotalPages();
        showPagedResults(rowId, responsePage, totalPages, addItemsToRow(rowId, movies, movieBinder()));
    }

    @Override
    public void showTvResponse(int rowId, TvShowResponse response) {
        List<TvShow> shows = response == null ? null : response.getResults();
        int responsePage = response == null ? currentPage(rowId) : response.getPage();
        int totalPages = response == null ? responsePage : response.getTotalPages();
        showPagedResults(rowId, responsePage, totalPages, addItemsToRow(rowId, shows, tvShowBinder()));
    }

    private RowItemBinder<Movie> movieBinder() {
        return new RowItemBinder<Movie>() {
            @Override
            public String key(Movie item) {
                return movieKey(item);
            }

            @Override
            public boolean canShow(Movie item) {
                return item != null && item.getPosterPath() != null;
            }
        };
    }

    private RowItemBinder<TvShow> tvShowBinder() {
        return new RowItemBinder<TvShow>() {
            @Override
            public String key(TvShow item) {
                return tvShowKey(item);
            }

            @Override
            public boolean canShow(TvShow item) {
                return item != null && item.getPosterPath() != null;
            }
        };
    }

    private int currentPage(int rowId) {
        MovieRow row = movieRowSparseArray.get(rowId);
        return row == null ? 1 : row.getPage();
    }

    private void showPagedResults(int rowId, int responsePage, int totalPages, int addedCount) {
        MovieRow row = movieRowSparseArray.get(rowId);
        RowLoadingHelper helper = helperFor(rowId);
        if (row == null || helper == null) {
            return;
        }

        int nextPage = Math.max(row.getPage(), responsePage + 1);
        boolean canRequestMore = totalPages <= 0 || responsePage < totalPages;
        row.setPage(nextPage);
        row.setLoading(false);
        row.setExhausted(!canRequestMore);
        helper.clearLoading(row.getAdapter());

        if (addedCount > 0) {
            row.setEmptyPageAttempts(0);
            if (!entranceTransitionStarted) {
                startEntranceTransition();
                entranceTransitionStarted = true;
            }
        } else if (canRequestMore) {
            int attempts = row.getEmptyPageAttempts() + 1;
            row.setEmptyPageAttempts(attempts);
            if (attempts >= MovieRow.MAX_EMPTY_PAGE_ATTEMPTS) {
                row.setExhausted(true);
                if (!row.isHasShownContent()) {
                    handleEmptyRow(rowId, row, helper);
                } else {
                    helper.showEmpty(row.getAdapter(), getString(R.string.empty_row));
                }
            } else {
                scheduleLoadNextPage(rowId);
            }
        } else if (!row.isHasShownContent()) {
            handleEmptyRow(rowId, row, helper);
        }
    }

    private void handleEmptyRow(int rowId, MovieRow row, RowLoadingHelper helper) {
        removeVisibleRow(rowId);
    }

    @Override
    public void showLoadError(int rowId, String source, Throwable throwable) {
        MovieRow row = movieRowSparseArray.get(rowId);
        RowLoadingHelper helper = helperFor(rowId);
        if (row == null || helper == null) {
            return;
        }
        row.setLoading(false);
        row.setError(true);
        row.setErrorMessage(getString(R.string.error_network));

        addVisibleRowIfNeeded(row);
        helper.showError(row.getAdapter(), getString(R.string.error_network), () -> {
            row.setError(false);
            helper.clearState(row.getAdapter());
            scheduleLoadNextPage(rowId);
        });
        Log.e(TAG, "load " + source + " error", throwable);
    }

    private void updateBackground(Object item) {
        if (item instanceof UiStateItem.Loading
                || item instanceof UiStateItem.Error
                || item instanceof UiStateItem.Retry
                || item instanceof UiStateItem.Empty) {
            return;
        }

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
        restoreSelectionIfNeeded();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_ROW, getSelectedPosition());
    }

    private void restoreSelectionIfNeeded() {
        if (pendingRestoreRow < 0 || rowsAdapter == null) {
            return;
        }
        if (pendingRestoreRow < rowsAdapter.size()) {
            int rowToRestore = pendingRestoreRow;
            pendingRestoreRow = -1;
            View root = getView();
            if (root != null) {
                root.post(() -> setSelectedPosition(rowToRestore, false));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rowsAdapter != null && rowsAdapter.size() > 0) {
            pendingRestoreRow = getSelectedPosition();
        }
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemVH, Object item,
                               RowPresenter.ViewHolder rowVH, Row row) {
        selectedItem = item;
        updateBackground(item);
        maybeLoadMore(row, item);
    }

    private void maybeLoadMore(Row row, Object item) {
        if (!(row instanceof ListRow)
                || item instanceof UiStateItem.Loading
                || item instanceof UiStateItem.Error
                || item instanceof UiStateItem.Retry
                || item instanceof UiStateItem.Empty) {
            return;
        }
        int rowId = (int) row.getHeaderItem().getId();
        MovieRow rowData = movieRowSparseArray.get(rowId);
        RowLoadingHelper helper = helperFor(rowId);
        if (rowData == null || helper == null) {
            return;
        }
        int contentSize = helper.getContentSize(rowData.getAdapter());
        int selectedIndex = rowData.getAdapter().indexOf(item);
        int threshold = Math.max(0, contentSize - 6);
        if (selectedIndex >= threshold) {
            scheduleLoadNextPage(rowId);
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof UiStateItem.Error || item instanceof UiStateItem.Retry) {
            return;
        }
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
