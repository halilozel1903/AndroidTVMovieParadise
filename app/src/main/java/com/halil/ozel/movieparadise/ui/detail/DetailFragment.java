package com.halil.ozel.movieparadise.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import androidx.leanback.widget.SparseArrayObjectAdapter;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.halil.ozel.movieparadise.App;
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
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.player.PlayerActivity;


import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DetailFragment extends DetailsFragment implements Palette.PaletteAsyncListener {

    public static String TRANSITION_NAME = "poster_transition";

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    Movie movie;
    MovieDetails movieDetails;
    ArrayObjectAdapter mAdapter;
    CustomDetailPresenter customDetailPresenter;
    DetailsOverviewRow detailsOverviewRow;
    ArrayObjectAdapter mCastAdapter = new ArrayObjectAdapter(new PersonPresenter());
    ArrayObjectAdapter mRecommendationsAdapter = new ArrayObjectAdapter(new MoviePresenter());
    String mYoutubeID;


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
            switch (actionId) {
                case 0:
                    if (mYoutubeID != null) {

                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra("videoId",mYoutubeID);
                        startActivity(intent);
                    }
                    break;
            }
        });



        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, customDetailPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(classPresenterSelector);
        setAdapter(mAdapter);
    }


    private void setUpDetailsOverviewRow() {
        detailsOverviewRow = new DetailsOverviewRow(new MovieDetails());
        mAdapter.add(detailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + movie.getPosterPath());
        fetchMovieDetails();
    }


    private void fetchMovieDetails() {
        theMovieDbAPI.getMovieDetails(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieDetails, e -> Timber.e(e, "Error fetching data: %s", e.getMessage()));
    }

    private void fetchCastMembers() {
        theMovieDbAPI.getCredits(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, e -> Timber.e(e, "Error fetching data: %s", e.getMessage()));
    }

    private void setUpCastMembers() {
        mAdapter.add(new ListRow(new HeaderItem(0, "Cast"), mCastAdapter));
        fetchCastMembers();
    }

    private void bindCastMembers(CreditsResponse response) {
        mCastAdapter.addAll(0, response.getCast());
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
        mAdapter.add(new ListRow(new HeaderItem(2, "Recommendations"), mRecommendationsAdapter));
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        theMovieDbAPI.getRecommendations(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindRecommendations, e -> Timber.e(e, "Error fetching recommendations: %s", e.getMessage()));
    }

    private void fetchVideos() {
        theMovieDbAPI.getMovieVideos(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> Timber.e(e, "Error fetching video response: %s", e.getMessage()));
    }

    private void handleVideoResponse(VideoResponse response) {
        mYoutubeID = getTrailer(response.getResults(), "official");

        if (mYoutubeID == null) {
            mYoutubeID = getTrailer(response.getResults(), "trailer");
        }

        if (mYoutubeID == null) {
            mYoutubeID = getTrailerByType(response.getResults(), "trailer");
        }

        if (mYoutubeID != null) {
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
    }

    private SimpleTarget<GlideDrawable> mGlideDrawableSimpleTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            detailsOverviewRow.setImageDrawable(resource);
        }
    };


    private void loadImage(String url) {
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        changePalette(((GlideBitmapDrawable) resource).getBitmap());
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
        int index = mAdapter.indexOf(detailsOverviewRow);
        mAdapter.notifyArrayItemRangeChanged(index, 1);
    }
}
