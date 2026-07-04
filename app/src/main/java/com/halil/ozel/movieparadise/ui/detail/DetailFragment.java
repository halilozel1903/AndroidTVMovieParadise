package com.halil.ozel.movieparadise.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.data.models.CreditsResponse;
import com.halil.ozel.movieparadise.data.models.CrewMember;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieDetails;
import com.halil.ozel.movieparadise.data.models.PaletteColors;
import com.halil.ozel.movieparadise.data.models.VideoResponse;
import com.halil.ozel.movieparadise.data.models.WatchProvider;
import com.halil.ozel.movieparadise.data.models.WatchProvidersResponse;
import com.halil.ozel.movieparadise.ui.base.PaletteUtils;
import com.halil.ozel.movieparadise.ui.base.TvRows;
import com.halil.ozel.movieparadise.ui.common.RowLoadingHelper;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.genre.GenreMoviesActivity;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DetailFragment extends DetailsSupportFragment
        implements Palette.PaletteAsyncListener, OnItemViewClickedListener {

    public static final String TRANSITION_NAME = "poster_transition";
    private static final String TAG = "DetailFragment";
    private static final int CAST_SKELETON_COUNT = 6;
    private static final int RECOMMENDATIONS_SKELETON_COUNT = 6;

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private Movie movie;
    private MovieDetails movieDetails;
    private ArrayObjectAdapter arrayObjectAdapter;
    private CustomDetailPresenter customDetailPresenter;
    private DetailsOverviewRow detailsOverviewRow;
    private final RowLoadingHelper castLoadingHelper = new RowLoadingHelper();
    private final RowLoadingHelper recommendationsLoadingHelper = new RowLoadingHelper();
    private final ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(
            castLoadingHelper.createSelector(new PersonPresenter(), CastMember.class));
    private final ArrayObjectAdapter mRecommendationsAdapter = new ArrayObjectAdapter(
            recommendationsLoadingHelper.createSelector(new MoviePresenter(), Movie.class));
    private final RecommendationRowHelper<Movie> recommendationsHelper =
            new RecommendationRowHelper<>(
                    mRecommendationsAdapter,
                    Movie::getId,
                    Movie::getPosterPath,
                    new RecommendationRowHelper.ResultListener() {
                        @Override
                        public void onRecommendationsReady() {
                            recommendationsLoadingHelper.clearLoading(mRecommendationsAdapter);
                            int castIndex = arrayObjectAdapter.indexOf(castRow);
                            addRowIfMissing(mRecommendationsRow, getRecommendationsInsertIndex());
                        }

                        @Override
                        public void onRecommendationsEmpty() {
                            recommendationsLoadingHelper.clearLoading(mRecommendationsAdapter);
                            removeRow(mRecommendationsRow);
                        }
                    });
    private ListRow castRow;
    private ListRow mRecommendationsRow;
    private final DetailTagRowsHelper tagRowsHelper = new DetailTagRowsHelper(this::openGenreMovies);
    private String youtubeID;
    private boolean detailsLoadFailed;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public static DetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(Movie.class.getSimpleName(), movie);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);

        if (getArguments() == null || !getArguments().containsKey(Movie.class.getSimpleName())) {
            Log.e(TAG, "DetailFragment launched without required Movie argument");
            if (getActivity() != null) {
                // Close the host activity gracefully instead of crashing
                getActivity().finish();
            }
            return;
        }
        movie = getArguments().getParcelable(Movie.class.getSimpleName());

        if (movie == null) {
            Log.e(TAG, "Movie parcelable is null in DetailFragment");
            if (getActivity() != null) getActivity().finish();
            return;
        }

        Log.d(TAG, "DetailFragment received movie: id=" + movie.getId() + " title=" + movie.getTitle());

        setUpAdapter();
        setUpDetailsOverviewRow();
        setUpCastMembers();
        setupRecommendationsRow();
        setOnItemViewClickedListener(this);
    }

    private void setUpAdapter() {
        customDetailPresenter = new CustomDetailPresenter(
                new DetailDescriptionPresenter(this::openGenreMovies), new DetailsOverviewLogoPresenter());

        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        customDetailPresenter.setListener(helper);
        customDetailPresenter.setParticipatingEntranceTransition(false);

        customDetailPresenter.setOnActionClickedListener(action -> {
            int actionId = (int) action.getId();
            if (actionId == TrailerHelper.ACTION_TRAILER && youtubeID != null && getActivity() != null) {
                startActivity(TrailerHelper.createPlayerIntent(requireActivity(), youtubeID));
            } else if (actionId == TrailerHelper.ACTION_RETRY_DETAILS) {
                detailsLoadFailed = false;
                fetchMovieDetails();
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
        // Show basic movie info immediately by creating MovieDetails from the passed Movie.
        movieDetails = new MovieDetails(movie);
        detailsOverviewRow = new DetailsOverviewRow(movieDetails);
        arrayObjectAdapter.add(detailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + movie.getPosterPath());
        fetchMovieDetails();
        fetchVideos();
        fetchWatchProviders();
    }

    private void fetchWatchProviders() {
        if (movie == null || movie.getId() == null) {
            return;
        }
        disposables.add(theMovieDbAPI.getMovieWatchProviders(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (movieDetails != null) {
                        movieDetails.setWatchProviders(WatchProvidersHelper.pickProviders(response));
                        refreshTagRows();
                    }
                }, e -> Log.e(TAG, "fetchWatchProviders error", e)));
    }

    private void fetchMovieDetails() {
        if (movie == null || movie.getId() == null) {
            Log.w(TAG, "fetchMovieDetails skipped: movie is null");
            return;
        }

        Log.d(TAG, "Fetching movie details for id=" + movie.getId());

        disposables.add(theMovieDbAPI.getMovieDetails(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieDetails, e -> {
                    Log.e(TAG, "fetchMovieDetails error", e);
                    detailsLoadFailed = true;
                    updateOverviewActions();
                }));
    }

    private void fetchCastMembers() {
        if (movie == null || movie.getId() == null) {
            removeRow(castRow);
            return;
        }
        disposables.add(theMovieDbAPI.getCredits(movie.getId(), Config.API_KEY_URL)
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

    private void setUpCastMembers() {
        castRow = new ListRow(new HeaderItem(0, getString(R.string.cast_label)), castAdapter);
        castLoadingHelper.showInitialLoading(castAdapter, CAST_SKELETON_COUNT);
        addRowIfMissing(castRow, getCastRowInsertIndex());
        fetchCastMembers();
    }

    private void bindCastMembers(CreditsResponse response) {
        castLoadingHelper.clearLoading(castAdapter);
        castLoadingHelper.clearState(castAdapter);
        if (response == null || response.getCast() == null) {
            return;
        }
        if (response.getCast().isEmpty()) {
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
        // Find director from crew list
        if (response.getCrew() == null) {
            return;
        }
        response.getCrew().stream()
                .filter(c -> "Director".equals(c.getJob()))
                .findFirst()
                .ifPresent(c -> {
                    if (movieDetails != null) {
                        movieDetails.setDirector(c.getName());
                        notifyDetailsChanged();
                    }
                });
    }

    private void bindMovieDetails(MovieDetails details) {
        Log.d(TAG, "bindMovieDetails: details=" + (details == null ? "null" : details.getTitle()));
        if (details == null) {
            return;
        }
        PaletteColors paletteColors = movieDetails == null ? null : movieDetails.getPaletteColors();
        String director = movieDetails == null ? null : movieDetails.getDirector();
        List<WatchProvider> watchProviders = movieDetails == null ? null : movieDetails.getWatchProviders();
        this.movieDetails = details;
        if (paletteColors != null) {
            this.movieDetails.setPaletteColors(paletteColors);
        }
        if (director != null) {
            this.movieDetails.setDirector(director);
        }
        if (watchProviders != null) {
            this.movieDetails.setWatchProviders(watchProviders);
        }
        detailsLoadFailed = false;
        detailsOverviewRow.setItem(this.movieDetails);
        refreshTagRows();
        updateOverviewActions();
    }

    private void setupRecommendationsRow() {
        mRecommendationsRow = new ListRow(
                new HeaderItem(2, getString(R.string.recommendations_label)), mRecommendationsAdapter);
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        if (movie == null || movie.getId() == null) {
            removeRow(mRecommendationsRow);
            return;
        }
        recommendationsHelper.cancel();
        recommendationsHelper.start(movie.getId(), 2);
        recommendationsLoadingHelper.showInitialLoading(mRecommendationsAdapter, RECOMMENDATIONS_SKELETON_COUNT);
        addRowIfMissing(mRecommendationsRow, getRecommendationsInsertIndex());

        disposables.add(theMovieDbAPI.getRecommendations(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            recommendationsLoadingHelper.clearLoading(mRecommendationsAdapter);
                            recommendationsHelper.append(
                                    response == null ? null : response.getResults());
                        },
                        e -> {
                            Log.e(TAG, "fetchRecommendations error", e);
                            recommendationsHelper.finishRequest();
                        }));

        disposables.add(theMovieDbAPI.getSimilarMovies(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            recommendationsLoadingHelper.clearLoading(mRecommendationsAdapter);
                            recommendationsHelper.append(
                                    response == null ? null : response.getResults());
                        },
                        e -> {
                            Log.e(TAG, "fetchSimilarMovies error", e);
                            recommendationsHelper.finishRequest();
                        }));
    }

    private void fetchVideos() {
        disposables.add(theMovieDbAPI.getMovieVideos(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> Log.e(TAG, "fetchVideos error", e)));
    }

    private void handleVideoResponse(VideoResponse response) {
        youtubeID = TrailerHelper.findYoutubeTrailerId(response);
        updateOverviewActions();
    }

    private void updateOverviewActions() {
        if (detailsOverviewRow == null) {
            return;
        }
        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
        boolean hasActions = false;

        if (youtubeID != null) {
            adapter.set(TrailerHelper.ACTION_TRAILER,
                    new Action(TrailerHelper.ACTION_TRAILER, getString(R.string.watch_trailer)));
            hasActions = true;
        }
        if (detailsLoadFailed) {
            adapter.set(TrailerHelper.ACTION_RETRY_DETAILS,
                    new Action(TrailerHelper.ACTION_RETRY_DETAILS, getString(R.string.details_load_error)));
            hasActions = true;
        }

        detailsOverviewRow.setActionsAdapter(hasActions ? adapter : null);
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

    private final CustomTarget<Drawable> mGlideTarget = new CustomTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            detailsOverviewRow.setImageDrawable(resource);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            // no-op
        }
    };

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            Glide.with(requireActivity()).load(R.drawable.popcorn).into(mGlideTarget);
            return;
        }
        Glide.with(requireActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                               Target<Drawable> target, boolean first) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target,
                                                   DataSource source, boolean first) {
                        if (resource instanceof BitmapDrawable) {
                            changePalette(((BitmapDrawable) resource).getBitmap());
                        }
                        return false;
                    }
                })
                .into(mGlideTarget);
    }

    private void changePalette(Bitmap bmp) {
        Palette.from(bmp).generate(this);
    }

    @Override
    public void onGenerated(Palette palette) {
        PaletteColors colors = PaletteUtils.getPaletteColors(palette);
        customDetailPresenter.setActionsBackgroundColor(colors.getStatusBarColor());
        customDetailPresenter.setBackgroundColor(colors.getToolbarBackgroundColor());
        if (movieDetails != null) {
            movieDetails.setPaletteColors(colors);
        }
        notifyDetailsChanged();
    }

    private void notifyDetailsChanged() {
        if (movieDetails == null) {
            Object item = detailsOverviewRow.getItem();
            if (item instanceof MovieDetails) {
                movieDetails = (MovieDetails) item;
            } else {
                return;
            }
        }
        detailsOverviewRow.setItem(this.movieDetails);
        int index = arrayObjectAdapter.indexOf(detailsOverviewRow);
        if (index >= 0) {
            arrayObjectAdapter.notifyArrayItemRangeChanged(index, 1);
        }
    }

    private void refreshTagRows() {
        if (movieDetails == null || getContext() == null) {
            return;
        }
        tagRowsHelper.updateGenres(arrayObjectAdapter, requireContext(), movieDetails.getGenres());
        tagRowsHelper.updateProviders(arrayObjectAdapter, requireContext(), movieDetails.getWatchProviders());
    }

    private void openGenreMovies(Genre genre) {
        if (genre == null || getActivity() == null) {
            return;
        }
        Intent intent = GenreMoviesActivity.newIntent(requireActivity(), genre);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie) {
            Movie clicked = (Movie) item;
            Intent intent = MediaDetailActivity.newMovieIntent(requireActivity(), clicked);

            if (itemVH.view instanceof MovieCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((MovieCardView) itemVH.view).getPosterIV(),
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
        } else if (item instanceof Genre) {
            openGenreMovies((Genre) item);
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
