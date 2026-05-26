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
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.data.models.TvShowResponse;
import com.halil.ozel.movieparadise.ui.base.GlideBackgroundManager;
import com.halil.ozel.movieparadise.ui.detail.DetailActivity;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.search.SearchActivity;
import com.halil.ozel.movieparadise.ui.tv.TvDetailActivity;
import com.halil.ozel.movieparadise.ui.tv.TvDetailFragment;
import com.halil.ozel.movieparadise.ui.tv.TvShowCardView;
import com.halil.ozel.movieparadise.ui.tv.TvShowPresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragment extends BrowseSupportFragment
        implements OnItemViewSelectedListener, OnItemViewClickedListener {

    private static final String TAG = "MainFragment";

    // Row index constants
    private static final int NOW_PLAYING    = 0;
    private static final int TOP_RATED      = 1;
    private static final int POPULAR        = 2;
    private static final int UPCOMING       = 3;
    private static final int TV_ON_THE_AIR  = 4;
    private static final int TV_AIRING_TODAY= 5;
    private static final int TV_POPULAR     = 6;
    private static final int TV_TOP_RATED   = 7;

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private GlideBackgroundManager glideBackgroundManager;
    private Object selectedItem;
    private SparseArray<MovieRow> movieRowSparseArray;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        setSearchAffordanceColor(ContextCompat.getColor(requireContext(), R.color.accent_color));

        setOnSearchClickedListener(v -> {
            startActivity(new Intent(requireActivity(), SearchActivity.class));
        });

        createDataRows();
        createRows();
        prepareEntranceTransition();

        fetchNowPlayingMovies();
        fetchTopRatedMovies();
        fetchPopularMovies();
        fetchUpcomingMovies();
        fetchOnTheAir();
        fetchAiringToday();
        fetchPopularTv();
        fetchTopRatedTv();
    }

    private void createDataRows() {
        movieRowSparseArray = new SparseArray<>();

        MoviePresenter moviePresenter = new MoviePresenter();
        TvShowPresenter tvPresenter   = new TvShowPresenter();

        movieRowSparseArray.put(NOW_PLAYING,     newMovieRow(NOW_PLAYING,     "Now Playing",   moviePresenter));
        movieRowSparseArray.put(TOP_RATED,       newMovieRow(TOP_RATED,       "Top Rated",     moviePresenter));
        movieRowSparseArray.put(POPULAR,         newMovieRow(POPULAR,         "Popular",       moviePresenter));
        movieRowSparseArray.put(UPCOMING,        newMovieRow(UPCOMING,        "Upcoming",      moviePresenter));
        movieRowSparseArray.put(TV_ON_THE_AIR,   newMovieRow(TV_ON_THE_AIR,   "On The Air",    tvPresenter));
        movieRowSparseArray.put(TV_AIRING_TODAY, newMovieRow(TV_AIRING_TODAY, "Airing Today",  tvPresenter));
        movieRowSparseArray.put(TV_POPULAR,      newMovieRow(TV_POPULAR,      "Popular TV",    tvPresenter));
        movieRowSparseArray.put(TV_TOP_RATED,    newMovieRow(TV_TOP_RATED,    "Top Rated TV",  tvPresenter));
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

    // ── Fetch helpers ────────────────────────────────────────────────────────

    private void fetchNowPlayingMovies() {
        disposables.add(theMovieDbAPI.getNowPlayingMovies(Config.API_KEY_URL, movieRowSparseArray.get(NOW_PLAYING).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindMovieResponse(r, NOW_PLAYING); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchNowPlaying error", e)));
    }

    private void fetchTopRatedMovies() {
        disposables.add(theMovieDbAPI.getTopRatedMovies(Config.API_KEY_URL, movieRowSparseArray.get(TOP_RATED).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindMovieResponse(r, TOP_RATED); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchTopRated error", e)));
    }

    private void fetchPopularMovies() {
        disposables.add(theMovieDbAPI.getPopularMovies(Config.API_KEY_URL, movieRowSparseArray.get(POPULAR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindMovieResponse(r, POPULAR); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchPopular error", e)));
    }

    private void fetchUpcomingMovies() {
        disposables.add(theMovieDbAPI.getUpcomingMovies(Config.API_KEY_URL, movieRowSparseArray.get(UPCOMING).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindMovieResponse(r, UPCOMING); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchUpcoming error", e)));
    }

    private void fetchOnTheAir() {
        disposables.add(theMovieDbAPI.getOnTheAir(Config.API_KEY_URL, movieRowSparseArray.get(TV_ON_THE_AIR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, TV_ON_THE_AIR); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchOnTheAir error", e)));
    }

    private void fetchAiringToday() {
        disposables.add(theMovieDbAPI.getAiringToday(Config.API_KEY_URL, movieRowSparseArray.get(TV_AIRING_TODAY).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, TV_AIRING_TODAY); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchAiringToday error", e)));
    }

    private void fetchPopularTv() {
        disposables.add(theMovieDbAPI.getPopularTv(Config.API_KEY_URL, movieRowSparseArray.get(TV_POPULAR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, TV_POPULAR); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchPopularTv error", e)));
    }

    private void fetchTopRatedTv() {
        disposables.add(theMovieDbAPI.getTopRatedTv(Config.API_KEY_URL, movieRowSparseArray.get(TV_TOP_RATED).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, TV_TOP_RATED); startEntranceTransition(); },
                           e -> Log.e(TAG, "fetchTopRatedTv error", e)));
    }

    // ── Bind helpers ─────────────────────────────────────────────────────────

    private void bindMovieResponse(MovieResponse response, int rowId) {
        MovieRow row = movieRowSparseArray.get(rowId);
        row.setPage(row.getPage() + 1);
        for (Movie movie : response.getResults()) {
            if (movie.getPosterPath() != null) {
                row.getAdapter().add(movie);
            }
        }
    }

    private void bindTvResponse(TvShowResponse response, int rowId) {
        MovieRow row = movieRowSparseArray.get(rowId);
        row.setPage(row.getPage() + 1);
        for (TvShow show : response.getResults()) {
            if (show.getPosterPath() != null) {
                row.getAdapter().add(show);
            }
        }
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
        disposables.clear();
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

        } else if (item instanceof TvShow) {
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
