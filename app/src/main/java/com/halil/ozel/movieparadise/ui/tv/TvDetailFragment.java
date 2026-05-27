package com.halil.ozel.movieparadise.ui.tv;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.data.models.CreditsResponse;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.detail.CustomDetailPresenter;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.detail.PersonDetailActivity;
import com.halil.ozel.movieparadise.ui.detail.PersonPresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/** Detail fragment for TV shows. */
public class TvDetailFragment extends DetailsSupportFragment implements OnItemViewClickedListener {

    public static String TRANSITION_NAME = "poster_transition";

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private TvShow tvShow;
    private ArrayObjectAdapter arrayObjectAdapter;
    private CustomDetailPresenter customDetailPresenter;
    private DetailsOverviewRow detailsOverviewRow;
    private final ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(new PersonPresenter());
    private final ArrayObjectAdapter recommendationsAdapter = new ArrayObjectAdapter(new TvShowPresenter());
    private ListRow castRow;
    private ListRow recommendationsRow;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final CustomTarget<Drawable> mGlideDrawableSimpleTarget = new CustomTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            detailsOverviewRow.setImageDrawable(resource);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            // no-op
        }
    };

    public static TvDetailFragment newInstance(TvShow show) {
        Bundle args = new Bundle();
        args.putParcelable(TvShow.class.getSimpleName(), show);
        TvDetailFragment fragment = new TvDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        if (getArguments() == null || !getArguments().containsKey(TvShow.class.getSimpleName())) {
            throw new RuntimeException("A tv show is necessary for TvDetailFragment");
        }
        tvShow = getArguments().getParcelable(TvShow.class.getSimpleName());
        setUpAdapter();
        setUpDetailsOverviewRow();
        setupCastRow();
        setupRecommendationsRow();
        setOnItemViewClickedListener(this);
    }

    private void setUpAdapter() {
        customDetailPresenter = new CustomDetailPresenter(new TvDetailDescriptionPresenter(), new DetailsOverviewLogoPresenter());
        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        customDetailPresenter.setListener(helper);
        customDetailPresenter.setParticipatingEntranceTransition(false);

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, customDetailPresenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
        arrayObjectAdapter = new ArrayObjectAdapter(selector);
        setAdapter(arrayObjectAdapter);
    }

    private void setUpDetailsOverviewRow() {
        detailsOverviewRow = new DetailsOverviewRow(tvShow);
        arrayObjectAdapter.add(detailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + tvShow.getPosterPath());
        fetchTvShowDetails();
    }

    private void setupCastRow() {
        castRow = new ListRow(new HeaderItem(0, getString(R.string.cast_label)), castAdapter);
        fetchCastMembers();
    }

    private void setupRecommendationsRow() {
        recommendationsRow = new ListRow(
                new HeaderItem(1, getString(R.string.recommendations_label)), recommendationsAdapter);
        fetchRecommendations();
    }

    private void fetchTvShowDetails() {
        if (tvShow == null || tvShow.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getTvShowDetails(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindTvShowDetails, ignored -> {
                }));
    }

    private void fetchCastMembers() {
        if (tvShow == null || tvShow.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getTvCredits(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, ignored -> removeRow(castRow)));
    }

    private void fetchRecommendations() {
        if (tvShow == null || tvShow.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getTvRecommendations(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                        removeRow(recommendationsRow);
                        return;
                    }
                    for (TvShow recommendation : response.getResults()) {
                        if (recommendation.getPosterPath() != null) {
                            recommendationsAdapter.add(recommendation);
                        }
                    }
                    if (recommendationsAdapter.size() > 0) {
                        int castIndex = arrayObjectAdapter.indexOf(castRow);
                        addRowIfMissing(recommendationsRow, castIndex >= 0 ? castIndex + 1 : 1);
                    } else {
                        removeRow(recommendationsRow);
                    }
                }, ignored -> removeRow(recommendationsRow)));
    }

    private void bindCastMembers(CreditsResponse response) {
        if (response == null || response.getCast() == null || response.getCast().isEmpty()) {
            removeRow(castRow);
            return;
        }
        for (CastMember castMember : response.getCast()) {
            if (castMember.getName() != null) {
                castAdapter.add(castMember);
            }
        }
        if (castAdapter.size() > 0) {
            addRowIfMissing(castRow, 1);
        } else {
            removeRow(castRow);
        }
    }

    private void bindTvShowDetails(TvShow details) {
        if (details == null) {
            return;
        }
        boolean expanded = tvShow != null && tvShow.isDetailsExpanded();
        tvShow = details;
        tvShow.setDetailsExpanded(expanded);
        detailsOverviewRow.setItem(tvShow);
        int index = arrayObjectAdapter.indexOf(detailsOverviewRow);
        if (index >= 0) {
            arrayObjectAdapter.notifyArrayItemRangeChanged(index, 1);
        }
    }

    private void addRowIfMissing(ListRow row, int index) {
        if (row == null || arrayObjectAdapter.indexOf(row) >= 0) {
            return;
        }
        int safeIndex = Math.max(0, Math.min(index, arrayObjectAdapter.size()));
        arrayObjectAdapter.add(safeIndex, row);
    }

    private void removeRow(ListRow row) {
        if (row != null && arrayObjectAdapter.indexOf(row) >= 0) {
            arrayObjectAdapter.remove(row);
        }
    }

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            Glide.with(this)
                    .load(R.drawable.popcorn)
                    .into(mGlideDrawableSimpleTarget);
            return;
        }
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
                .into(mGlideDrawableSimpleTarget);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof TvShow) {
            TvShow clicked = (TvShow) item;
            Intent intent = MediaDetailActivity.newTvIntent(requireActivity(), clicked);

            if (itemVH.view instanceof TvShowCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((TvShowCardView) itemVH.view).getPosterIV(),
                        TRANSITION_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        } else if (item instanceof CastMember) {
            CastMember cast = (CastMember) item;
            Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
            intent.putExtra(CastMember.class.getSimpleName(), cast);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
