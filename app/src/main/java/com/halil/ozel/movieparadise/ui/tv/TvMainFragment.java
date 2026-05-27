package com.halil.ozel.movieparadise.ui.tv;

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
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.main.MovieRow;

import java.util.List;

import javax.inject.Inject;

/** Fragment that lists TV series categories. */
public class TvMainFragment extends BrowseSupportFragment
        implements OnItemViewSelectedListener, OnItemViewClickedListener, TvMainContract.View {

    private static final String TAG = "TvMainFragment";

    @Inject
    TvMainPresenter presenter;

    private GlideBackgroundManager glideBackgroundManager;
    private TvShow selectedShow;
    private SparseArray<MovieRow> tvRowSparseArray;
    private SparseArray<ListRow> visibleRows;
    private ArrayObjectAdapter rowsAdapter;

    public static TvMainFragment newInstance() {
        TvMainFragment fragment = new TvMainFragment();
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

        createDataRows();
        setupRowsAdapter();
        prepareEntranceTransition();
        presenter.loadSections(rowId -> {
            MovieRow rowData = tvRowSparseArray.get(rowId);
            return rowData == null ? 1 : rowData.getPage();
        });
    }

    private void createDataRows() {
        tvRowSparseArray = new SparseArray<>();
        visibleRows = new SparseArray<>();
        TvShowPresenter presenter = new TvShowPresenter();

        tvRowSparseArray.put(TvMainContract.ON_THE_AIR,   newRow(TvMainContract.ON_THE_AIR,   "On The Air",   presenter));
        tvRowSparseArray.put(TvMainContract.AIRING_TODAY, newRow(TvMainContract.AIRING_TODAY, "Airing Today", presenter));
        tvRowSparseArray.put(TvMainContract.POPULAR,      newRow(TvMainContract.POPULAR,      "Popular TV",   presenter));
        tvRowSparseArray.put(TvMainContract.TOP_RATED,    newRow(TvMainContract.TOP_RATED,    "Top Rated TV", presenter));
    }

    private MovieRow newRow(int id, String title, Presenter presenter) {
        return new MovieRow()
                .setId(id)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle(title)
                .setPage(1);
    }

    private void setupRowsAdapter() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
        setAdapter(rowsAdapter);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    private void addTvShowsToRow(int rowId, List<TvShow> shows) {
        MovieRow row = tvRowSparseArray.get(rowId);
        if (row == null || shows == null || shows.isEmpty()) {
            return;
        }
        int oldSize = row.getAdapter().size();
        for (TvShow show : shows) {
            if (show.getPosterPath() != null) {
                row.getAdapter().add(show);
            }
        }
        if (row.getAdapter().size() > oldSize) {
            row.setPage(row.getPage() + 1);
            addVisibleRowIfNeeded(row);
        }
    }

    private void addVisibleRowIfNeeded(MovieRow row) {
        if (visibleRows.get(row.getId()) != null) {
            return;
        }
        ListRow listRow = new ListRow(new HeaderItem(row.getId(), row.getTitle()), row.getAdapter());
        visibleRows.put(row.getId(), listRow);
        rowsAdapter.add(visibleRowIndex(row.getId()), listRow);
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
    public void showTvResults(int rowId, List<TvShow> shows) {
        addTvShowsToRow(rowId, shows);
        startEntranceTransition();
    }

    @Override
    public void showLoadError(String source, Throwable throwable) {
        Log.e(TAG, "load " + source + " error", throwable);
    }

    // ── Background ───────────────────────────────────────────────────────────

    private void updateBackground(TvShow show) {
        if (show == null) return;
        if (show.getBackdropPath() != null) {
            glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + show.getBackdropPath());
        } else {
            glideBackgroundManager.setBackground(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.material_bg));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBackground(selectedShow);
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
        if (item instanceof TvShow) {
            selectedShow = (TvShow) item;
            updateBackground(selectedShow);
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof TvShow) {
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
