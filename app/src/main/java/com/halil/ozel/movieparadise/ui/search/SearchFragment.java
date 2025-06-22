package com.halil.ozel.movieparadise.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.DataSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
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

public class SearchFragment extends androidx.leanback.app.SearchSupportFragment
        implements androidx.leanback.app.SearchSupportFragment.SearchResultProvider, OnItemViewClickedListener {

    @Inject
    TheMovieDbAPI theMovieDbAPI;
    MovieResponse movieResponse;
    Movie movie;
    ArrayObjectAdapter arrayAdapter;
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
        arrayAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);
        setupSearchRow();
    }


    @Override
    public ObjectAdapter getResultsAdapter() {
        return arrayAdapter;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        theMovieDbAPI.getSearchMovies(query, true, Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindSearch, e -> {
                    loadImage(movie.getPosterPath());
                    bindMovieDetails(movieResponse);
                    performSearch();
                    System.out.println(e.getMessage());
                });

        performSearch();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        theMovieDbAPI.getSearchMovies(query,true, Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindSearch, e -> {
                    loadImage(movie.getPosterPath());
                    bindMovieDetails(movieResponse);
                    performSearch();
                    System.out.println(e.getMessage());
                });

        performSearch();
        return true;
    }


    private void bindMovieDetails(MovieResponse movieResponse) {
        this.movieResponse = movieResponse;
    }


    private void setupSearchRow() {
        arrayAdapter.add(new ListRow(new HeaderItem(0, "" + ""), arrayObjectAdapter));
        setOnItemViewClickedListener(this);
    }


    private void bindSearch(MovieResponse responseObj) {
        arrayObjectAdapter.addAll(0, responseObj.getResults());
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
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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
