package com.halil.ozel.movieparadise.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
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
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.ui.detail.DetailActivity;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends androidx.leanback.app.SearchSupportFragment
        implements androidx.leanback.app.SearchSupportFragment.SearchResultProvider,
                   OnItemViewClickedListener {

    private static final String TAG = "SearchFragment";

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private ArrayObjectAdapter rowsAdapter;
    private final ArrayObjectAdapter resultsAdapter = new ArrayObjectAdapter(new MoviePresenter());
    private final CompositeDisposable disposables = new CompositeDisposable();
    @Nullable
    private Disposable searchDisposable;
    private String lastQuery = "";

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);

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
        String normalizedQuery = query == null ? "" : query.trim();
        if (normalizedQuery.equals(lastQuery)) {
            return true;
        }

        lastQuery = normalizedQuery;
        resultsAdapter.clear();
        cancelActiveSearch();
        if (normalizedQuery.isEmpty()) return true;

        searchDisposable = theMovieDbAPI.getSearchMovies(normalizedQuery, true, Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (normalizedQuery.equals(lastQuery) && response.getResults() != null) {
                                resultsAdapter.addAll(0, response.getResults());
                            }
                        },
                           e -> Log.e(TAG, "Search error", e));
        disposables.add(searchDisposable);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    private void cancelActiveSearch() {
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }
        searchDisposable = null;
    }

    @Override
    public void onDestroy() {
        cancelActiveSearch();
        disposables.clear();
        super.onDestroy();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder viewHolder, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie movie) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Movie.class.getSimpleName(), movie);

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
