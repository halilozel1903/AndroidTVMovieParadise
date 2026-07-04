package com.halil.ozel.movieparadise.ui.tv;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;

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
import com.halil.ozel.movieparadise.data.models.VideoResponse;
import com.halil.ozel.movieparadise.data.models.WatchProvider;
import com.halil.ozel.movieparadise.ui.common.RowLoadingHelper;
import com.halil.ozel.movieparadise.ui.base.TvRows;
import com.halil.ozel.movieparadise.ui.detail.CustomDetailPresenter;
import com.halil.ozel.movieparadise.ui.detail.DetailDescriptionPresenter;
import com.halil.ozel.movieparadise.ui.detail.MediaDetailActivity;
import com.halil.ozel.movieparadise.ui.detail.PersonDetailActivity;
import com.halil.ozel.movieparadise.ui.detail.PersonPresenter;
import com.halil.ozel.movieparadise.ui.detail.DetailTagRowsHelper;
import com.halil.ozel.movieparadise.ui.detail.RecommendationRowHelper;
import com.halil.ozel.movieparadise.ui.detail.TagListRow;
import com.halil.ozel.movieparadise.ui.detail.TrailerHelper;
import com.halil.ozel.movieparadise.ui.detail.WatchProvidersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/** Detail fragment for TV shows. */
public class TvDetailFragment extends DetailsSupportFragment implements OnItemViewClickedListener {

    private static final String TAG = "TvDetailFragment";
    private static final int CAST_SKELETON_COUNT = 6;
    private static final int RECOMMENDATIONS_SKELETON_COUNT = 6;

    public static String TRANSITION_NAME = "poster_transition";

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private TvShow tvShow;
    private ArrayObjectAdapter arrayObjectAdapter;
    private CustomDetailPresenter customDetailPresenter;
    private DetailsOverviewRow detailsOverviewRow;
    private final RowLoadingHelper castLoadingHelper = new RowLoadingHelper();
    private final RowLoadingHelper recommendationsLoadingHelper = new RowLoadingHelper();
    private final ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(
            castLoadingHelper.createSelector(new PersonPresenter(), CastMember.class));
    private final ArrayObjectAdapter recommendationsAdapter = new ArrayObjectAdapter(
            recommendationsLoadingHelper.createSelector(new TvShowPresenter(), TvShow.class));
    private final RecommendationRowHelper<TvShow> recommendationsHelper =
            new RecommendationRowHelper<>(
                    recommendationsAdapter,
                    TvShow::getId,
                    TvShow::getPosterPath,
                    new RecommendationRowHelper.ResultListener() {
                        @Override
                        public void onRecommendationsReady() {
                            recommendationsLoadingHelper.clearLoading(recommendationsAdapter);
                            int castIndex = arrayObjectAdapter.indexOf(castRow);
                            addRowIfMissing(recommendationsRow, getRecommendationsInsertIndex());
                        }

                        @Override
                        public void onRecommendationsEmpty() {
                            recommendationsLoadingHelper.clearLoading(recommendationsAdapter);
                            removeRow(recommendationsRow);
                        }
                    });
    private ListRow castRow;
    private ListRow recommendationsRow;
    private final DetailTagRowsHelper tagRowsHelper = new DetailTagRowsHelper(null);
    private String youtubeID;
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
        customDetailPresenter = new CustomDetailPresenter(
                new DetailDescriptionPresenter(null), new DetailsOverviewLogoPresenter());
        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        customDetailPresenter.setListener(helper);
        customDetailPresenter.setParticipatingEntranceTransition(false);

        customDetailPresenter.setOnActionClickedListener(action -> {
            int actionId = (int) action.getId();
            if (actionId == TrailerHelper.ACTION_TRAILER && youtubeID != null && getActivity() != null) {
                startActivity(TrailerHelper.createPlayerIntent(requireActivity(), youtubeID));
            }
        });

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, customDetailPresenter);
        selector.addClassPresenter(TagListRow.class, TvRows.tagRowPresenter());
        selector.addClassPresenter(ListRow.class, TvRows.listRowPresenter());
        arrayObjectAdapter = new ArrayObjectAdapter(selector);
        setAdapter(arrayObjectAdapter);
    }

    private void setUpDetailsOverviewRow() {
        detailsOverviewRow = new DetailsOverviewRow(tvShow);
        arrayObjectAdapter.add(detailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + tvShow.getPosterPath());
        fetchTvShowDetails();
        fetchVideos();
        fetchWatchProviders();
    }

    private void fetchVideos() {
        if (tvShow == null || tvShow.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getTvVideos(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> Log.e(TAG, "fetchVideos error", e)));
    }

    private void handleVideoResponse(VideoResponse response) {
        youtubeID = TrailerHelper.findYoutubeTrailerId(response);
        updateOverviewActions();
    }

    private void fetchWatchProviders() {
        if (tvShow == null || tvShow.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getTvWatchProviders(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (tvShow != null) {
                        tvShow.setWatchProviders(WatchProvidersHelper.pickProviders(response));
                        refreshTagRows();
                    }
                }, e -> Log.e(TAG, "fetchWatchProviders error", e)));
    }

    private void notifyDetailsChanged() {
        if (detailsOverviewRow == null || tvShow == null) {
            return;
        }
        detailsOverviewRow.setItem(tvShow);
        notifyOverviewRowChanged();
    }

    private void updateOverviewActions() {
        if (detailsOverviewRow == null) {
            return;
        }
        if (youtubeID == null) {
            detailsOverviewRow.setActionsAdapter(null);
            notifyOverviewRowChanged();
            return;
        }
        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
        adapter.set(TrailerHelper.ACTION_TRAILER,
                new Action(TrailerHelper.ACTION_TRAILER, getString(R.string.watch_trailer)));
        detailsOverviewRow.setActionsAdapter(adapter);
        notifyOverviewRowChanged();
    }

    private void notifyOverviewRowChanged() {
        if (detailsOverviewRow == null || arrayObjectAdapter == null) {
            return;
        }
        int index = arrayObjectAdapter.indexOf(detailsOverviewRow);
        if (index >= 0) {
            arrayObjectAdapter.notifyArrayItemRangeChanged(index, 1);
        }
    }

    private void refreshTagRows() {
        if (tvShow == null || getContext() == null) {
            return;
        }
        tagRowsHelper.updateGenres(arrayObjectAdapter, requireContext(), tvShow.getGenres());
        tagRowsHelper.updateProviders(arrayObjectAdapter, requireContext(), tvShow.getWatchProviders());
    }

    private void setupCastRow() {
        castRow = new ListRow(new HeaderItem(0, getString(R.string.cast_label)), castAdapter);
        castLoadingHelper.showInitialLoading(castAdapter, CAST_SKELETON_COUNT);
        addRowIfMissing(castRow, getCastRowInsertIndex());
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
                .subscribe(this::bindTvShowDetails, e -> Log.e(TAG, "fetchTvShowDetails error", e)));
    }

    private void fetchCastMembers() {
        if (tvShow == null || tvShow.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getTvCredits(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, e -> {
                    Log.e(TAG, "fetchCastMembers error", e);
                    castLoadingHelper.showError(
                            castAdapter,
                            getString(R.string.error_network),
                            this::fetchCastMembers);
                }));
    }

    private void fetchRecommendations() {
        if (tvShow == null || tvShow.getId() == null) {
            removeRow(recommendationsRow);
            return;
        }
        recommendationsHelper.cancel();
        recommendationsHelper.start(tvShow.getId(), 2);
        recommendationsLoadingHelper.showInitialLoading(recommendationsAdapter, RECOMMENDATIONS_SKELETON_COUNT);
        addRowIfMissing(recommendationsRow, getRecommendationsInsertIndex());

        disposables.add(theMovieDbAPI.getTvRecommendations(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            recommendationsLoadingHelper.clearLoading(recommendationsAdapter);
                            recommendationsHelper.append(
                                    response == null ? null : response.getResults());
                        },
                        e -> {
                            Log.e(TAG, "fetchRecommendations error", e);
                            recommendationsHelper.finishRequest();
                        }));

        disposables.add(theMovieDbAPI.getSimilarTvShows(tvShow.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            recommendationsLoadingHelper.clearLoading(recommendationsAdapter);
                            recommendationsHelper.append(
                                    response == null ? null : response.getResults());
                        },
                        e -> {
                            Log.e(TAG, "fetchSimilarTvShows error", e);
                            recommendationsHelper.finishRequest();
                        }));
    }

    private void bindCastMembers(CreditsResponse response) {
        castLoadingHelper.clearLoading(castAdapter);
        castLoadingHelper.clearState(castAdapter);
        if (response == null || response.getCast() == null || response.getCast().isEmpty()) {
            removeRow(castRow);
            return;
        }
        castAdapter.clear();
        for (CastMember castMember : response.getCast()) {
            if (castMember.getName() != null) {
                castAdapter.add(castMember);
            }
        }
        if (castAdapter.size() > 0) {
            addRowIfMissing(castRow, getCastRowInsertIndex());
        } else {
            removeRow(castRow);
        }
    }

    private void bindTvShowDetails(TvShow details) {
        if (details == null) {
            return;
        }
        List<WatchProvider> watchProviders = tvShow == null ? null : tvShow.getWatchProviders();
        tvShow = details;
        if (watchProviders != null) {
            tvShow.setWatchProviders(watchProviders);
        }
        notifyDetailsChanged();
        refreshTagRows();
        updateOverviewActions();
    }

    private int getCastRowInsertIndex() {
        return tagRowsHelper.getCastInsertIndex(arrayObjectAdapter);
    }

    private int getRecommendationsInsertIndex() {
        int castIndex = arrayObjectAdapter.indexOf(castRow);
        return castIndex >= 0 ? castIndex + 1 : getCastRowInsertIndex();
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
