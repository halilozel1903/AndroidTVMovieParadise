package com.halil.ozel.movieparadise.ui.tv;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
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
import com.halil.ozel.movieparadise.ui.tv.TvDetailFragment;
import com.halil.ozel.movieparadise.ui.tv.TvDetailActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/** Fragment that lists TV series categories. */
public class TvMainFragment extends BrowseFragment implements OnItemViewSelectedListener, OnItemViewClickedListener {

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    GlideBackgroundManager glideBackgroundManager;

    private TvShow selectedShow;

    private static final int ON_THE_AIR = 0;
    private static final int AIRING_TODAY = 1;
    private static final int POPULAR = 2;
    private static final int TOP_RATED = 3;

    SparseArray<MovieRow> tvRowSparseArray;

    public static TvMainFragment newInstance() {
        Bundle args = new Bundle();
        TvMainFragment fragment = new TvMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.instance().appComponent().inject(this);

        glideBackgroundManager = new GlideBackgroundManager(getActivity());

        setBrandColor(ContextCompat.getColor(getActivity(), R.color.primary_transparent));
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

        tvRowSparseArray.put(ON_THE_AIR, new MovieRow()
                .setId(ON_THE_AIR)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle("On The Air")
                .setPage(1)
        );

        tvRowSparseArray.put(AIRING_TODAY, new MovieRow()
                .setId(AIRING_TODAY)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle("Airing Today")
                .setPage(1)
        );

        tvRowSparseArray.put(POPULAR, new MovieRow()
                .setId(POPULAR)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle("Popular TV")
                .setPage(1)
        );

        tvRowSparseArray.put(TOP_RATED, new MovieRow()
                .setId(TOP_RATED)
                .setAdapter(new ArrayObjectAdapter(presenter))
                .setTitle("Top Rated TV")
                .setPage(1)
        );
    }

    private void createRows() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        for (int i = 0; i < tvRowSparseArray.size(); i++) {
            MovieRow row = tvRowSparseArray.get(i);
            HeaderItem headerItem = new HeaderItem(row.getId(), row.getTitle());
            ListRow listRow = new ListRow(headerItem, row.getAdapter());
            rowsAdapter.add(listRow);
        }
        setAdapter(rowsAdapter);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    private void fetchOnTheAir() {
        theMovieDbAPI.getOnTheAir(Config.API_KEY_URL, tvRowSparseArray.get(ON_THE_AIR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, ON_THE_AIR); startEntranceTransition(); }, e -> System.out.println(e.getMessage()));
    }

    private void fetchAiringToday() {
        theMovieDbAPI.getAiringToday(Config.API_KEY_URL, tvRowSparseArray.get(AIRING_TODAY).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, AIRING_TODAY); startEntranceTransition(); }, e -> System.out.println(e.getMessage()));
    }

    private void fetchPopular() {
        theMovieDbAPI.getPopularTv(Config.API_KEY_URL, tvRowSparseArray.get(POPULAR).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, POPULAR); startEntranceTransition(); }, e -> System.out.println(e.getMessage()));
    }

    private void fetchTopRated() {
        theMovieDbAPI.getTopRatedTv(Config.API_KEY_URL, tvRowSparseArray.get(TOP_RATED).getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> { bindTvResponse(r, TOP_RATED); startEntranceTransition(); }, e -> System.out.println(e.getMessage()));
    }

    private void bindTvResponse(TvShowResponse response, int id) {
        MovieRow row = tvRowSparseArray.get(id);
        row.setPage(row.getPage() + 1);
        for (TvShow tvShow : response.getResults()) {
            if (tvShow.getPosterPath() != null) {
                row.getAdapter().add(tvShow);
            }
        }
    }

    private void updateBackground(TvShow show) {
        if (show != null) {
            if (show.getBackdropPath() != null) {
                glideBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + show.getBackdropPath());
            } else {
                glideBackgroundManager.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.material_bg));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBackground(selectedShow);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof TvShow) {
            selectedShow = (TvShow) item;
            updateBackground(selectedShow);
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof TvShow) {
            TvShow tvShow = (TvShow) item;
            Intent intent = new Intent(getActivity(), TvDetailActivity.class);
            intent.putExtra(TvShow.class.getSimpleName(), tvShow);

            if (itemViewHolder.view instanceof TvShowCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((TvShowCardView) itemViewHolder.view).getPosterIV(),
                        TvDetailFragment.TRANSITION_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }
}
