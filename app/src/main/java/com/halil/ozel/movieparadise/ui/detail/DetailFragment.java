package com.halil.ozel.movieparadise.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.DetailsFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.CreditsResponse;
import com.halil.ozel.movieparadise.data.models.CrewMember;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieDetails;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.data.models.PaletteColors;
import com.halil.ozel.movieparadise.data.models.Video;
import com.halil.ozel.movieparadise.data.models.VideoResponse;
import com.halil.ozel.movieparadise.ui.base.PaletteUtils;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.player.PlayerActivity;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.ui.detail.PersonDetailActivity;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailFragment extends DetailsFragment implements Palette.PaletteAsyncListener, OnItemViewClickedListener {

    public static String TRANSITION_NAME = "poster_transition";

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    Movie movie;
    MovieDetails movieDetails;
    ArrayObjectAdapter arrayObjectAdapter;
    CustomDetailPresenter customDetailPresenter;
    DetailsOverviewRow detailsOverviewRow;
    ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(new PersonPresenter());
    ArrayObjectAdapter mRecommendationsAdapter = new ArrayObjectAdapter(new MoviePresenter());
    ListRow mRecommendationsRow;
    String youtubeID;


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
            throw new RuntimeException("A movie is necessary for DetailFragment");
        }

        movie = getArguments().getParcelable(Movie.class.getSimpleName());
        setUpAdapter();
        setUpDetailsOverviewRow();
        setUpCastMembers();
        setupRecommendationsRow();
        setOnItemViewClickedListener(this);
    }


    private void setUpAdapter() {
        customDetailPresenter = new CustomDetailPresenter(new DetailDescriptionPresenter(),
                new DetailsOverviewLogoPresenter());

        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        customDetailPresenter.setListener(helper);
        customDetailPresenter.setParticipatingEntranceTransition(false);

        customDetailPresenter.setOnActionClickedListener(action -> {
            int actionId = (int) action.getId();
            if (actionId == 0) {
                if (youtubeID != null) {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("videoId", youtubeID);
                    startActivity(intent);
                }
            }
        });

        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, customDetailPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        arrayObjectAdapter = new ArrayObjectAdapter(classPresenterSelector);
        setAdapter(arrayObjectAdapter);
    }


    private void setUpDetailsOverviewRow() {
        detailsOverviewRow = new DetailsOverviewRow(new MovieDetails());
        arrayObjectAdapter.add(detailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + movie.getPosterPath());
        fetchMovieDetails();
    }


    private void fetchMovieDetails() {
        theMovieDbAPI.getMovieDetails(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieDetails, e -> System.out.println(e.getMessage()));
    }

    private void fetchCastMembers() {
        theMovieDbAPI.getCredits(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, e -> System.out.println(e.getMessage()));
    }

    private void setUpCastMembers() {
        arrayObjectAdapter.add(new ListRow(new HeaderItem(0, "Cast"), castAdapter));
        fetchCastMembers();
    }

    private void bindCastMembers(CreditsResponse response) {
        castAdapter.addAll(0, response.getCast());
        if (!response.getCrew().isEmpty()) {
            for(CrewMember c : response.getCrew()) {
                if (c.getJob().equals("Director")) {
                    movieDetails.setDirector(c.getName());
                    notifyDetailsChanged();
                }
            }
        }
    }

    private void bindMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
        detailsOverviewRow.setItem(this.movieDetails);
        fetchVideos();
    }

    private void setupRecommendationsRow() {
        mRecommendationsRow = new ListRow(new HeaderItem(2, "Recommendations"), mRecommendationsAdapter);
        arrayObjectAdapter.add(mRecommendationsRow);
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        theMovieDbAPI.getRecommendations(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindRecommendations, e -> System.out.println(e.getMessage()));
    }

    private void fetchVideos() {
        theMovieDbAPI.getMovieVideos(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> System.out.println(e.getMessage()));
    }

    private void handleVideoResponse(VideoResponse response) {
        youtubeID = getTrailer(response.getResults(), "official");

        if (youtubeID == null) {
            youtubeID = getTrailer(response.getResults(), "trailer");
        }

        if (youtubeID == null) {
            youtubeID = getTrailerByType(response.getResults(), "trailer");
        }

        if (youtubeID != null) {
            SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
            adapter.set(0, new Action(0, "WATCH TRAILER", null, null));
            detailsOverviewRow.setActionsAdapter(adapter);
            notifyDetailsChanged();
        }
    }

    private String getTrailer(List<Video> videos, String keyword) {
        String id = null;
        for(Video video : videos) {
            if (video.getName().toLowerCase().contains(keyword)) {
                id = video.getKey();
            }
        }
        return id;
    }

    private String getTrailerByType(List<Video> videos, String keyword) {
        String id = null;
        for(Video v : videos) {
            if (v.getType().toLowerCase().contains(keyword)) {
                id = v.getKey();
            }
        }
        return id;
    }

    private void bindRecommendations(MovieResponse response) {
        mRecommendationsAdapter.addAll(0, response.getResults());
        if (response.getResults() == null || response.getResults().isEmpty()) {
            arrayObjectAdapter.remove(mRecommendationsRow);
        }
    }

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


    private void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            Glide.with(getActivity())
                    .load(R.drawable.popcorn)
                    .into(mGlideDrawableSimpleTarget);
            return;
        }
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof BitmapDrawable) {
                            changePalette(((BitmapDrawable) resource).getBitmap());
                        }
                        return false;
                    }
                })
                .into(mGlideDrawableSimpleTarget);
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
            this.movieDetails.setPaletteColors(colors);
        }
        notifyDetailsChanged();
    }

    private void notifyDetailsChanged() {
        detailsOverviewRow.setItem(this.movieDetails);
        int index = arrayObjectAdapter.indexOf(detailsOverviewRow);
        arrayObjectAdapter.notifyArrayItemRangeChanged(index, 1);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Movie.class.getSimpleName(), movie);

            if (itemViewHolder.view instanceof MovieCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((MovieCardView) itemViewHolder.view).getPosterIV(),
                        DetailFragment.TRANSITION_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
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
}
