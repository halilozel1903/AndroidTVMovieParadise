package com.halil.ozel.movieparadise.ui.detail;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.DetailsFragment;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.Config;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.Api.TheMovieDbAPI;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.data.models.Movie;
import com.halil.ozel.movieparadise.data.models.MovieCreditsResponse;
import com.halil.ozel.movieparadise.data.models.Person;
import com.halil.ozel.movieparadise.ui.movie.MovieCardView;
import com.halil.ozel.movieparadise.ui.movie.MoviePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PersonDetailFragment extends DetailsFragment implements OnItemViewClickedListener {

    @Inject
    TheMovieDbAPI theMovieDbAPI;

    private CastMember castMember;
    private Person person;
    private ArrayObjectAdapter adapter;
    private CustomDetailPresenter presenter;
    private DetailsOverviewRow detailsRow;
    private ArrayObjectAdapter movieAdapter = new ArrayObjectAdapter(new MoviePresenter());

    public static PersonDetailFragment newInstance(CastMember cast) {
        Bundle args = new Bundle();
        args.putParcelable(CastMember.class.getSimpleName(), cast);
        PersonDetailFragment fragment = new PersonDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        castMember = getArguments().getParcelable(CastMember.class.getSimpleName());
        setupAdapter();
        setupDetailsRow();
        setupMovieCredits();
        setOnItemViewClickedListener(this);
    }

    private void setupAdapter() {
        presenter = new CustomDetailPresenter(new PersonDetailDescriptionPresenter(), new DetailsOverviewLogoPresenter());
        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), DetailFragment.TRANSITION_NAME);
        presenter.setListener(helper);
        presenter.setParticipatingEntranceTransition(false);

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, presenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter());
        adapter = new ArrayObjectAdapter(selector);
        setAdapter(adapter);
    }

    private void setupDetailsRow() {
        detailsRow = new DetailsOverviewRow(new Person());
        adapter.add(detailsRow);
        loadImage(HttpClientModule.POSTER_URL + castMember.getProfilePath());
        fetchPersonDetails();
    }

    private void setupMovieCredits() {
        adapter.add(new ListRow(new HeaderItem(0, "Movies"), movieAdapter));
    }

    private void fetchPersonDetails() {
        theMovieDbAPI.getPerson(String.valueOf(castMember.getId()), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindPersonDetails, e -> System.out.println(e.getMessage()));
    }

    private void fetchMovieCredits() {
        theMovieDbAPI.getPersonMovieCredits(String.valueOf(castMember.getId()), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieCredits, e -> System.out.println(e.getMessage()));
    }

    private void bindPersonDetails(Person p) {
        this.person = p;
        detailsRow.setItem(this.person);
        fetchMovieCredits();
    }

    private void bindMovieCredits(MovieCreditsResponse response) {
        movieAdapter.addAll(0, response.getCast());
    }

    private void loadImage(String url) {
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(detailsRow.getImageView(getActivity()));
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
        }
    }
}
