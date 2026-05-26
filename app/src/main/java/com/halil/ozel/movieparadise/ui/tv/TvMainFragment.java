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
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.main.MovieRow;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/** Fragment that lists TV series categories. */
public class TvMainFragment extends BrowseSupportFragment
        implements OnItemViewSelectedListener, OnItemViewClickedListener {

    private static final String TAG = "TvMainFragment";

    private static final int ON_THE_AIR  = 0;
    private static final int AIRING_TODAY= 1;
    private static final int POPULAR     = 2;
    private static final int TOP_RATED   = 3;

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private GlideBackgroundManager glideBackgroundManager;
    private TvShow selectedShow;
    private SparseArray<MovieRow> tvRowSparseArray;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public static TvMainFragment newInstance() {
        TvMainFragment fragment = new TvMainFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.instance().appComponent().inject(this);

        glideBackgroundManager = new GlideBackgroundManager(requireActivity());

        setBrandColor(ContextCompat.getColor(requireContext(), R.color.primary_dark_transparent));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        createDataRows();
        createRows();
        prepareEntranceTransition();
        fetchOnTheAir();
        fetchAiringToday();
        fetchPopular();
        fetchTopRated();
    }

    private void createDataRows() {
        tvRowSparseArray = new SparseArray<>();
        TvShowPresenter presenter = new TvShowPresenter();

        tvRowSparseArray.put(ON_THE_AIR,   newRow(ON_THE_AIR,   "On The Air",   presenter));
        tvRowSparseArray.put(AIRING_TODAY, newRow(AIRING_TODAY, "Airing Today", presenter));
        tvRowSparseArray.put(POPULAR,      newRow(POPULAR,      "Popular TV",   presenter));
        tvRowSparseArray.put(TOP_RATED,    newRow(TOP_RATED,    "Top Rated TV", presenter));
    }

    private MovieRow newRow(int id, String title, Presenter presenter) {
        return new MovieRow()
                .setId(id)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle(title)
                .setPage(1);
    }

    private void createRows() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
        for (int i = 0; i < tvRowSparseArray.size(); i++) {
            MovieRow row = tvRowSparseArray.get(i);
            rowsAdapter.add(new ListRow(new HeaderItem(row.getId(), row.getTitle()), row.getAdapter()));
        }
        setAdapter(rowsAdapter);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    // ── Fetch helpers ────────────────────────────────────────────────────────

    private void fetchOnTheAir() {
        disposables.add(theMovieDbAPI.getOnTheAir(Config.API_KEY_URL, tvRowSparseArray.get(ON_THE_AIR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, ON_THE_AIR); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchOnTheAir error", e)));
    }

    private void fetchAiringToday() {
        disposables.add(theMovieDbAPI.getAiringToday(Config.API_KEY_URL, tvRowSparseArray.get(AIRING_TODAY).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, AIRING_TODAY); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchAiringToday error", e)));
    }

    private void fetchPopular() {
        disposables.add(theMovieDbAPI.getPopularTv(Config.API_KEY_URL, tvRowSparseArray.get(POPULAR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, POPULAR); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchPopular error", e)));
    }

    private void fetchTopRated() {
        disposables.add(theMovieDbAPI.getTopRatedTv(Config.API_KEY_URL, tvRowSparseArray.get(TOP_RATED).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, TOP_RATED); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchTopRated error", e)));
    }

    private void bindTvResponse(TvShowResponse response, int rowId) {
        MovieRow row = tvRowSparseArray.get(rowId);
        row.setPage(row.getPage() + 1);
        for (TvShow show : response.getResults()) {
            if (show.getPosterPath() != null) {
                row.getAdapter().add(show);
            }
        }
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
        disposables.clear();
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
            Intent intent = new Intent(requireActivity(), TvDetailActivity.class);
            intent.putExtra(TvShow.class.getSimpleName(), tvShow);

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
