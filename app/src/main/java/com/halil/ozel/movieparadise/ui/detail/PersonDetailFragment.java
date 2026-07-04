package com.halil.ozel.movieparadise.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.DetailsSupportFragment;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieCreditsResponse;
import com.halil.ozel.movieparadise.data.models.Person;
import com.halil.ozel.movieparadise.data.models.TvCreditsResponse;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.data.models.PersonImagesResponse;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.base.TvRows;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;
import com.halil.ozel.movieparadise.ui.tv.TvDetailFragment;
import com.halil.ozel.movieparadise.ui.tv.TvShowCardView;
import com.halil.ozel.movieparadise.ui.tv.TvShowPresenter;

import android.graphics.drawable.Drawable;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PersonDetailFragment extends DetailsSupportFragment implements OnItemViewClickedListener {

    private static final String TAG = "PersonDetailFragment";

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private CastMember castMember;
    private ArrayObjectAdapter adapter;
    private CustomDetailPresenter presenter;
    private DetailsOverviewRow detailsRow;
    private ListRow movieRow;
    private ListRow tvRow;
    private final ArrayObjectAdapter movieAdapter = new ArrayObjectAdapter(new MoviePresenter());
    private final ArrayObjectAdapter tvAdapter = new ArrayObjectAdapter(new TvShowPresenter());
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final CustomTarget<Drawable> mGlideTarget = new CustomTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            detailsRow.setImageDrawable(resource);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            // no-op
        }
    };

    public static PersonDetailFragment newInstance(CastMember cast) {
        Bundle args = new Bundle();
        if (cast != null) {
            args.putParcelable(CastMember.class.getSimpleName(), cast);
        }
        PersonDetailFragment fragment = new PersonDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);

        Bundle args = getArguments();
        if (args != null) {
            castMember = args.getParcelable(CastMember.class.getSimpleName());
        }

        if (castMember == null) {
            Log.w(TAG, "No CastMember passed to PersonDetailFragment");
            return;
        }

        setupAdapter();
        setupDetailsRow();
        fetchPersonImages();
        setOnItemViewClickedListener(this);
    }

    private void setupAdapter() {
        presenter = new CustomDetailPresenter(
                new PersonDetailDescriptionPresenter(), new DetailsOverviewLogoPresenter());

        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), DetailFragment.TRANSITION_NAME);
        presenter.setListener(helper);
        presenter.setParticipatingEntranceTransition(false);

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, presenter);
        selector.addClassPresenter(ListRow.class, TvRows.listRowPresenter());
        adapter = new ArrayObjectAdapter(selector);
        setAdapter(adapter);
    }

    private void setupDetailsRow() {
        detailsRow = new DetailsOverviewRow(new Person());
        adapter.add(detailsRow);
        if (castMember.getProfilePath() != null) {
            loadPortraitImage(castMember.getProfilePath());
        }
        fetchPersonDetails();
    }

    private void setupMovieRow() {
        movieRow = new ListRow(new HeaderItem(1, getString(R.string.movies_label)), movieAdapter);
    }

    private void setupTvRow() {
        tvRow = new ListRow(new HeaderItem(2, getString(R.string.series_label)), tvAdapter);
    }

    private void fetchPersonDetails() {
        disposables.add(theMovieDbAPI.getPerson(String.valueOf(castMember.getId()), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindPersonDetails, e -> Log.e(TAG, "fetchPersonDetails error", e)));
    }

    private void fetchMovieCredits() {
        disposables.add(theMovieDbAPI.getPersonMovieCredits(String.valueOf(castMember.getId()), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieCredits, e -> Log.e(TAG, "fetchMovieCredits error", e)));
    }

    private void bindPersonDetails(Person p) {
        detailsRow.setItem(p);
        setupMovieRow();
        setupTvRow();
        fetchMovieCredits();
        fetchTvCredits();
    }

    private void bindMovieCredits(MovieCreditsResponse response) {
        movieAdapter.clear();
        if (response != null && response.getCast() != null) {
            for (Movie movie : response.getCast()) {
                if (movie != null && movie.getPosterPath() != null) {
                    movieAdapter.add(movie);
                }
            }
        }
        updateCreditRow(movieRow, movieAdapter);
    }

    private void fetchTvCredits() {
        disposables.add(theMovieDbAPI.getPersonTvCredits(String.valueOf(castMember.getId()), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindTvCredits, e -> Log.e(TAG, "fetchTvCredits error", e)));
    }

    private void bindTvCredits(TvCreditsResponse response) {
        tvAdapter.clear();
        if (response != null && response.getCast() != null) {
            for (TvShow show : response.getCast()) {
                if (show != null && show.getPosterPath() != null) {
                    tvAdapter.add(show);
                }
            }
        }
        updateCreditRow(tvRow, tvAdapter);
    }

    private void updateCreditRow(ListRow row, ArrayObjectAdapter rowAdapter) {
        if (row == null || rowAdapter.size() == 0) {
            removeCreditRow(row);
            return;
        }
        if (adapter.indexOf(row) < 0) {
            adapter.add(row);
        }
    }

    private void removeCreditRow(ListRow row) {
        if (row != null) {
            int index = adapter.indexOf(row);
            if (index >= 0) {
                adapter.remove(row);
            }
        }
    }

    private void fetchPersonImages() {
        disposables.add(theMovieDbAPI.getPersonImages(String.valueOf(castMember.getId()), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::applyPersonImages, e -> {
                    Log.e(TAG, "fetchPersonImages error", e);
                    applyPersonImages(null);
                }));
    }

    private void applyPersonImages(PersonImagesResponse response) {
        String portraitPath = PersonImageHelper.pickPortraitPath(response, castMember.getProfilePath());
        if (portraitPath != null) {
            loadPortraitImage(portraitPath);
        }
        String backdropPath = PersonImageHelper.pickBackdropPath(response, portraitPath);
        if (getActivity() instanceof PersonBackgroundHost) {
            ((PersonBackgroundHost) getActivity()).updatePersonBackground(backdropPath);
        }
    }

    private void loadPortraitImage(String profilePath) {
        loadImage(HttpClientModule.PROFILE_URL + profilePath);
    }

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        Glide.with(requireActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
                .fitCenter()
                .into(mGlideTarget);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemVH, Object item,
                              RowPresenter.ViewHolder rowVH, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent intent = MediaDetailActivity.newMovieIntent(requireActivity(), movie);

            if (itemVH.view instanceof MovieCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((MovieCardView) itemVH.view).getPosterIV(),
                        DetailFragment.TRANSITION_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        } else if (item instanceof TvShow) {
            TvShow tvShow = (TvShow) item;
            Intent intent = MediaDetailActivity.newTvIntent(requireActivity(), tvShow);

            if (itemVH.view instanceof TvShowCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((TvShowCardView) itemVH.view).getPosterIV(),
                        TvDetailFragment.TRANSITION_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
