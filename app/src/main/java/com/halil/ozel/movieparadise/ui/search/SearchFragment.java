package com.halil.ozel.movieparadise.ui.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieResponse;
import com.halil.ozel.movieparadise.ui.detail.DetailActivity;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SearchFragment extends androidx.leanback.app.SearchFragment
        implements androidx.leanback.app.SearchFragment.SearchResultProvider, OnItemViewClickedListener {


    @Inject
    TheMovieDbAPI theMovieDbAPI;

    MovieResponse movieResponse;

    Movie movie;

    ArrayObjectAdapter mAdapter;

    DetailsOverviewRow detailsOverviewRow;

    ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new MoviePresenter());


    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);

        mAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        setSearchResultProvider(this);

        setupSearchRow();


    }


    @Override
    public ObjectAdapter getResultsAdapter() {

        return mAdapter;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        theMovieDbAPI.getSearchMovies(query, Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindSearch, e -> {
                    loadImage(movie.getPosterPath());
                    bindMovieDetails(movieResponse);
                    performSearch();
                    Timber.e(e, "Error fetching search response: %query", e.getMessage());
                });

        performSearch();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        theMovieDbAPI.getSearchMovies(query, Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindSearch, e -> {
                    loadImage(movie.getPosterPath());
                    bindMovieDetails(movieResponse);
                    performSearch();
                    Timber.e(e, "Error fetching search response: %query", e.getMessage());
                });

        performSearch();

        return true;
    }


    private void bindMovieDetails(MovieResponse movieResponse) {
        this.movieResponse = movieResponse;
    }


    private void setupSearchRow() {
        mAdapter.add(new ListRow(new HeaderItem(0, "" + ""), arrayObjectAdapter));
        setOnItemViewClickedListener(this);

    }


    private void bindSearch(MovieResponse responseObj) {
        arrayObjectAdapter.addAll(0, responseObj.getResults());
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
                        return false;
                    }
                })
                .into(mGlideDrawableSimpleTarget);
    }


    private void performSearch() {

        arrayObjectAdapter.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        setSearchQuery(data, true);
    }


    @Override
    public void onItemClicked(Presenter.ViewHolder viewHolder, Object item, RowPresenter.ViewHolder itemViewHolder, Row row) {

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
        }

    }

}
