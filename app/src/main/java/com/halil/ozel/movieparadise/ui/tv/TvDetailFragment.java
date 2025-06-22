package com.halil.ozel.movieparadise.ui.tv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.app.DetailsFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.TvShow;
import com.halil.ozel.movieparadise.ui.detail.CustomDetailPresenter;
import com.halil.ozel.movieparadise.ui.tv.TvDetailDescriptionPresenter;

/** Very lightweight detail fragment for TV shows. */
public class TvDetailFragment extends DetailsFragment {

    public static String TRANSITION_NAME = "poster_transition";

    private TvShow tvShow;
    private ArrayObjectAdapter arrayObjectAdapter;
    private CustomDetailPresenter customDetailPresenter;
    private DetailsOverviewRow detailsOverviewRow;

    public static TvDetailFragment newInstance(TvShow show) {
        Bundle args = new Bundle();
        args.putParcelable(TvShow.class.getSimpleName(), show);
        TvDetailFragment fragment = new TvDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(TvShow.class.getSimpleName())) {
            throw new RuntimeException("A tv show is necessary for TvDetailFragment");
        }
        tvShow = getArguments().getParcelable(TvShow.class.getSimpleName());
        setUpAdapter();
        setUpDetailsOverviewRow();
    }

    private void setUpAdapter() {
        customDetailPresenter = new CustomDetailPresenter(new TvDetailDescriptionPresenter(), new DetailsOverviewLogoPresenter());
        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        customDetailPresenter.setListener(helper);
        customDetailPresenter.setParticipatingEntranceTransition(false);

        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(DetailsOverviewRow.class, customDetailPresenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter());
        arrayObjectAdapter = new ArrayObjectAdapter(selector);
        setAdapter(arrayObjectAdapter);
    }

    private void setUpDetailsOverviewRow() {
        detailsOverviewRow = new DetailsOverviewRow(tvShow);
        arrayObjectAdapter.add(detailsOverviewRow);
        loadImage(HttpClientModule.POSTER_URL + tvShow.getPosterPath());
    }

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
                .into(new ImageView(getActivity()));
    }
}
