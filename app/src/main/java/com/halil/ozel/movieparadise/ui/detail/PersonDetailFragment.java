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
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieCreditsResponse;
import com.halil.ozel.movieparadise.data.models.Person;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

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
    private final ArrayObjectAdapter movieAdapter = new ArrayObjectAdapter(new MoviePresenter());
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
        setupMovieCredits();
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
        selector.addClassPresenter(ListRow.class, new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_SMALL));
        adapter = new ArrayObjectAdapter(selector);
        setAdapter(adapter);
    }

    private void setupDetailsRow() {
        detailsRow = new DetailsOverviewRow(new Person());
        adapter.add(detailsRow);
        if (castMember.getProfilePath() != null) {
            loadImage(HttpClientModule.POSTER_URL + castMember.getProfilePath());
        }
        fetchPersonDetails();
    }

    private void setupMovieCredits() {
        adapter.add(new ListRow(new HeaderItem(0, getString(R.string.movies_label)), movieAdapter));
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
        fetchMovieCredits();
    }

    private void bindMovieCredits(MovieCreditsResponse response) {
        movieAdapter.addAll(0, response.getCast());
    }

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) return;
        Glide.with(requireActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
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
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
