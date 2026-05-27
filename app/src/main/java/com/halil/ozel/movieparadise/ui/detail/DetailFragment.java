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
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
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
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.PaletteColors;
import com.halil.ozel.movieparadise.data.models.Video;
import com.halil.ozel.movieparadise.data.models.VideoResponse;
import com.halil.ozel.movieparadise.ui.base.PaletteUtils;
import com.halil.ozel.movieparadise.ui.genre.GenreMoviesActivity;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.player.PlayerActivity;

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

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private Movie movie;
    private MovieDetails movieDetails;
    private ArrayObjectAdapter arrayObjectAdapter;
    private CustomDetailPresenter customDetailPresenter;
    private DetailsOverviewRow detailsOverviewRow;
    private final ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(new PersonPresenter());
    private final ArrayObjectAdapter mRecommendationsAdapter = new ArrayObjectAdapter(new MoviePresenter());
    private ListRow castRow;
    private ListRow mRecommendationsRow;
    private String youtubeID;
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
            if ((int) action.getId() == 0 && youtubeID != null) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("videoId", youtubeID);
                startActivity(intent);
            }
        });

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, customDetailPresenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
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
    }

    private void fetchMovieDetails() {
        if (movie == null) {
            Log.w(TAG, "fetchMovieDetails skipped: movie is null");
            return;
        }

        Log.d(TAG, "Fetching movie details for id=" + movie.getId());

        disposables.add(theMovieDbAPI.getMovieDetails(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieDetails, e -> Log.e(TAG, "fetchMovieDetails error", e)));
    }

    private void fetchCastMembers() {
        disposables.add(theMovieDbAPI.getCredits(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, e -> Log.e(TAG, "fetchCastMembers error", e)));
    }

    private void setUpCastMembers() {
        castRow = new ListRow(new HeaderItem(0, getString(R.string.cast_label)), castAdapter);
        fetchCastMembers();
    }

    private void bindCastMembers(CreditsResponse response) {
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
            addRowIfMissing(castRow, 1);
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
        this.movieDetails = details;
        if (paletteColors != null) {
            this.movieDetails.setPaletteColors(paletteColors);
        }
        if (director != null) {
            this.movieDetails.setDirector(director);
        }
        detailsOverviewRow.setItem(this.movieDetails);
        // Ensure UI is refreshed for the details overview row
        notifyDetailsChanged();
    }

    private void setupRecommendationsRow() {
        mRecommendationsRow = new ListRow(
                new HeaderItem(2, getString(R.string.recommendations_label)), mRecommendationsAdapter);
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        disposables.add(theMovieDbAPI.getRecommendations(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindRecommendations, e -> Log.e(TAG, "fetchRecommendations error", e)));
    }

    private void fetchVideos() {
        disposables.add(theMovieDbAPI.getMovieVideos(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> Log.e(TAG, "fetchVideos error", e)));
    }

    private void handleVideoResponse(VideoResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return;
        }
        // Priority: official trailer → name contains "trailer" → type "Trailer"
        youtubeID = findVideoKey(response.getResults(), "official");
        if (youtubeID == null) youtubeID = findVideoKey(response.getResults(), "trailer");
        if (youtubeID == null) youtubeID = findVideoKeyByType(response.getResults(), "trailer");

        if (youtubeID != null) {
            SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
            adapter.set(0, new Action(0, getString(R.string.watch_trailer)));
            detailsOverviewRow.setActionsAdapter(adapter);
            notifyDetailsChanged();
        }
    }

    private String findVideoKey(List<Video> videos, String keyword) {
        return videos.stream()
                .filter(v -> isYoutubeVideo(v)
                        && v.getName() != null
                        && v.getName().toLowerCase().contains(keyword))
                .map(Video::getKey)
                .findFirst()
                .orElse(null);
    }

    private String findVideoKeyByType(List<Video> videos, String keyword) {
        return videos.stream()
                .filter(v -> isYoutubeVideo(v)
                        && v.getType() != null
                        && v.getType().toLowerCase().contains(keyword))
                .map(Video::getKey)
                .findFirst()
                .orElse(null);
    }

    private boolean isYoutubeVideo(Video video) {
        return video != null
                && video.getKey() != null
                && video.getSite() != null
                && "youtube".equalsIgnoreCase(video.getSite());
    }

    private void bindRecommendations(MovieResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            removeRow(mRecommendationsRow);
        } else {
            mRecommendationsAdapter.clear();
            for (Movie recommendation : response.getResults()) {
                if (recommendation.getPosterPath() != null) {
                    mRecommendationsAdapter.add(recommendation);
                }
            }
            if (mRecommendationsAdapter.size() == 0) {
                removeRow(mRecommendationsRow);
            } else {
                int castIndex = arrayObjectAdapter.indexOf(castRow);
                addRowIfMissing(mRecommendationsRow, castIndex >= 0 ? castIndex + 1 : 1);
            }
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
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
