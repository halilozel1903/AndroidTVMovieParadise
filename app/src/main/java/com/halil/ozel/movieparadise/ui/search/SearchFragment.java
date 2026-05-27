package com.halil.ozel.movieparadise.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import java.util.List;

import javax.inject.Inject;

public class SearchFragment extends androidx.leanback.app.SearchSupportFragment
        implements androidx.leanback.app.SearchSupportFragment.SearchResultProvider,
                   OnItemViewClickedListener,
                   SearchContract.View {

    private static final String TAG = "SearchFragment";

    @Inject
    SearchPresenter presenter;

    private ArrayObjectAdapter rowsAdapter;
    private final ArrayObjectAdapter resultsAdapter = new ArrayObjectAdapter(new MoviePresenter());

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        presenter.attachView(this);

        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
        rowsAdapter.add(new ListRow(new HeaderItem(0, ""), resultsAdapter));

        setSearchResultProvider(this);
        setOnItemViewClickedListener(this);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return rowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        presenter.search(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public void clearResults() {
        resultsAdapter.clear();
    }

    @Override
    public void showResults(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return;
        }
        resultsAdapter.addAll(0, movies);
    }

    @Override
    public void showSearchError(Throwable throwable) {
        Log.e(TAG, "Search error", throwable);
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
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
