package com.halil.ozel.movieparadise.ui.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewLogoPresenter;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v7.graphics.Palette;

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
import com.halil.ozel.movieparadise.ui.movies.MoviePresenter;


import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version `1.0
 * @since 10/11/16.
 */
public class MovieDetailsFragment extends DetailsFragment implements Palette.PaletteAsyncListener {

    public static String TRANSITION_NAME = "poster_transition";

    // Injects the API using Dagger
    @Inject
    TheMovieDbAPI mDbAPI;

    private Movie movie;
    private MovieDetails movieDetails;
    private ArrayObjectAdapter mAdapter;
    private CustomMovieDetailsPresenter mFullWidthMovieDetailsPresenter;
    private DetailsOverviewRow mDetailsOverviewRow;
    private ArrayObjectAdapter mCastAdapter = new ArrayObjectAdapter(new PersonPresenter());
    private ArrayObjectAdapter mRecommendationsAdapter = new ArrayObjectAdapter(new MoviePresenter());
    String mYoutubeID;

    /**
     * Creates a new instance of a MovieDetailsFragment
     * @param movie
     *      The movie to be used by this fragment
     * @return
     *      A newly created instance of MovieDetailsFragment
     */
    public static MovieDetailsFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(Movie.class.getSimpleName(), movie);
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Injects this into the main component. Necessary for Dagger 2
        App.instance().appComponent().inject(this);
        if (getArguments() == null || !getArguments().containsKey(Movie.class.getSimpleName())) {
            throw new RuntimeException("An movie is necessary for MovieDetailsFragment");
        }

        // Retrieves the movie from the arguments
        movie = getArguments().getParcelable(Movie.class.getSimpleName());
        setUpAdapter();
        setUpDetailsOverviewRow();
        setUpCastMembers();
        setupRecommendationsRow();
    }

    /**
     * Sets up the adapter for this Fragment
     */
    private void setUpAdapter() {
        // Create the FullWidthPresenter
        mFullWidthMovieDetailsPresenter = new CustomMovieDetailsPresenter(new MovieDetailsDescriptionPresenter(),
                new DetailsOverviewLogoPresenter());

        // Handle the transition, the Helper is mainly used because the ActivityTransition is being passed from
        // The Activity into the Fragment
        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME); // the transition name is important
        mFullWidthMovieDetailsPresenter.setListener(helper); // Attach the listener
        // Define if this element is participating in the transition or not
        mFullWidthMovieDetailsPresenter.setParticipatingEntranceTransition(false);

        mFullWidthMovieDetailsPresenter.setOnActionClickedListener(action -> {
            int actionId = (int) action.getId();
            switch (actionId) {
                case 0:
                    if (mYoutubeID != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + mYoutubeID)));
                    }
                    break;
            }
        });


        // Class presenter selector allows the Adapter to render Rows and the details
        // It can be used in any of the Adapters by the Leanback library
        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFullWidthMovieDetailsPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(classPresenterSelector);
        setAdapter(mAdapter);
    }

    /**
     * Sets up the details overview rows
     */
    private void setUpDetailsOverviewRow() {
        mDetailsOverviewRow = new DetailsOverviewRow(new MovieDetails());
        // Add the DetailsOverviewRow to the adapter like we did on the MainFragment
        mAdapter.add(mDetailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + movie.getPosterPath());
        fetchMovieDetails();
    }

    /**
     * Fetches the movie details for a specific Movie.
     */
    private void fetchMovieDetails() {
        mDbAPI.getMovieDetails(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieDetails, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void fetchCastMembers() {
        mDbAPI.getCredits(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
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
        // Bind the details to the row
        mDetailsOverviewRow.setItem(this.movieDetails);
        fetchVideos();
    }

    private void setupRecommendationsRow() {
        mAdapter.add(new ListRow(new HeaderItem(2, "Recommendations"), mRecommendationsAdapter));
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        mDbAPI.getRecommendations(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindRecommendations, e -> {
                    Timber.e(e, "Error fetching recommendations: %s", e.getMessage());
                });
    }

    private void fetchVideos() {
        mDbAPI.getMovieVideos(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> {
                    Timber.e(e, "Error fetching video response: %s", e.getMessage());
                });
    }

    private void handleVideoResponse(VideoResponse response) {
        mYoutubeID = getTrailer(response.getResults(), "official");
        if (mYoutubeID == null) {
            mYoutubeID = getTrailer(response.getResults(), "trailer");
        }

        if (mYoutubeID == null) {
            mYoutubeID = getTrailer(response.getResults(), "teaser");
        }

        if (mYoutubeID == null) {
            mYoutubeID = getTrailerByType(response.getResults(), "trailer");
        }

        if (mYoutubeID == null) {
            mYoutubeID = getTrailerByType(response.getResults(), "featurette");
        }

        if (mYoutubeID != null) {
            SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
            adapter.set(0, new Action(0, "WATCH TRAILER", null, null));
            mDetailsOverviewRow.setActionsAdapter(adapter);
            notifyDetailsChanged();
        }
    }

    private String getTrailer(List<Video> videos, String keyword) {
        String id = null;
        for(Video v : videos) {
            if (v.getName().toLowerCase().contains(keyword)) {
                id = v.getKey();
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
            mDetailsOverviewRow.setImageDrawable(resource);
        }
    };

    /**
     * Loads the poster image into the DetailsOverviewRow
     * @param url
     *      The poster URL
     */
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

    /**
     * Generates a palette from a Bitmap
     * @param bmp
     *      The bitmap from which we want to generate the palette
     */
    private void changePalette(Bitmap bmp) {
        Palette.from(bmp).generate(this);
    }

    @Override
    public void onGenerated(Palette palette) {
        PaletteColors colors = PaletteUtils.getPaletteColors(palette);
        mFullWidthMovieDetailsPresenter.setActionsBackgroundColor(colors.getStatusBarColor());
        mFullWidthMovieDetailsPresenter.setBackgroundColor(colors.getToolbarBackgroundColor());
        if (movieDetails != null) {
            this.movieDetails.setPaletteColors(colors);
        }
        notifyDetailsChanged();
    }

    private void notifyDetailsChanged() {
        mDetailsOverviewRow.setItem(this.movieDetails);
        int index = mAdapter.indexOf(mDetailsOverviewRow);
        mAdapter.notifyArrayItemRangeChanged(index, 1);
    }
}
