package com.halil.ozel.movieparadise.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.common.RowLoadingHelper;
import com.halil.ozel.movieparadise.ui.base.TvRows;
import com.halil.ozel.movieparadise.ui.common.UiStateItem;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchFragment extends androidx.leanback.app.SearchSupportFragment
        implements androidx.leanback.app.SearchSupportFragment.SearchResultProvider,
                   OnItemViewClickedListener,
                   OnItemViewSelectedListener,
                   SearchContract.View {

    private static final String TAG = "SearchFragment";
    private static final String STATE_QUERY = "state_query";
    private static final int SEARCH_SKELETON_COUNT = 4;

    @Inject
    SearchPresenter presenter;

    private ArrayObjectAdapter rowsAdapter;
    private final RowLoadingHelper rowLoadingHelper = new RowLoadingHelper();
    private ArrayObjectAdapter resultsAdapter;
    private String pendingQuery;
    private String currentQuery = "";

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pendingQuery = savedInstanceState.getString(STATE_QUERY);
        }
        App.instance().appComponent().inject(this);
        presenter.attachView(this);

        resultsAdapter = new ArrayObjectAdapter(
                rowLoadingHelper.createSelector(new MoviePresenter(), Movie.class));
        rowsAdapter = new ArrayObjectAdapter(TvRows.listRowPresenter());
        rowsAdapter.add(new ListRow(new HeaderItem(0, getString(R.string.search_results_label)), resultsAdapter));

        setSearchResultProvider(this);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.search_title));

        if (pendingQuery != null && !pendingQuery.isEmpty()) {
            setSearchQuery(pendingQuery, true);
            pendingQuery = null;
        }
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return rowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        currentQuery = query == null ? "" : query.trim();
        presenter.search(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public void onSearchStarted() {
        rowLoadingHelper.clearState(resultsAdapter);
        rowLoadingHelper.showInitialLoading(resultsAdapter, SEARCH_SKELETON_COUNT);
    }

    @Override
    public void clearResults() {
        resultsAdapter.clear();
        rowLoadingHelper.clearLoading(resultsAdapter);
        rowLoadingHelper.clearState(resultsAdapter);
    }

    @Override
    public void showResults(List<Movie> movies) {
        rowLoadingHelper.clearLoading(resultsAdapter);
        rowLoadingHelper.clearState(resultsAdapter);
        if (movies == null || movies.isEmpty()) {
            return;
        }
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getPosterPath() != null) {
                filtered.add(movie);
            }
        }
        if (filtered.isEmpty()) {
            showEmpty();
            return;
        }
        resultsAdapter.addAll(0, filtered);
    }

    @Override
    public void showEmpty() {
        rowLoadingHelper.clearLoading(resultsAdapter);
        rowLoadingHelper.showEmpty(resultsAdapter, getString(R.string.empty_search));
    }

    @Override
    public void showSearchError(Throwable throwable) {
        rowLoadingHelper.showError(
                resultsAdapter,
                getString(R.string.error_network),
                () -> presenter.retryLastSearch());
        Log.e(TAG, "Search error", throwable);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!currentQuery.isEmpty()) {
            outState.putString(STATE_QUERY, currentQuery);
        }
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (!(getActivity() instanceof SearchActivity) || item instanceof UiStateItem.Loading
                || item instanceof UiStateItem.Error
                || item instanceof UiStateItem.Retry
                || item instanceof UiStateItem.Empty) {
            return;
        }
        if (item instanceof Movie movie) {
            ((SearchActivity) getActivity()).updateBackground(movie.getBackdropPath());
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder viewHolder, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie movie) {
            Intent intent = MediaDetailActivity.newMovieIntent(requireActivity(), movie);

            if (viewHolder.view instanceof MovieCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((MovieCardView) viewHolder.view).getPosterIV(),
                        DetailFragment.TRANSITION_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }
}
