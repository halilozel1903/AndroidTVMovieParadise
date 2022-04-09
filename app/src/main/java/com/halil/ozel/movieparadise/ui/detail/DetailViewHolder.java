package com.halil.ozel.movieparadise.ui.detail;

import android.graphics.drawable.GradientDrawable;
import androidx.leanback.widget.Presenter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.MovieDetails;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailViewHolder extends Presenter.ViewHolder {

    @BindView(R.id.movie_title)
    TextView movieTitleTV;

    @BindView(R.id.movie_year)
    TextView movieYearTV;

    @BindView(R.id.overview)
    TextView movieOverview;

    @BindView(R.id.runtime)
    TextView mRuntimeTV;

    @BindView(R.id.tagline)
    TextView mTaglineTV;

    @BindView(R.id.director_tv)
    TextView mDirectorTv;

    @BindView(R.id.overview_label)
    TextView mOverviewLabelTV;

    @BindView(R.id.genres)
    LinearLayout mGenresLayout;

    private View itemView;

    public DetailViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        itemView = view;
    }

    public void bind(MovieDetails movie) {
        if (movie != null && movie.getTitle() != null) {
            mRuntimeTV.setText(String.format(Locale.getDefault(), "%d minutes", movie.getRuntime()));
            mTaglineTV.setText(movie.getTagline());
            movieTitleTV.setText(movie.getTitle());
            movieYearTV.setText(String.format(Locale.getDefault(), "(%s)", movie.getReleaseDate().substring(0, 4)));
            movieOverview.setText(movie.getOverview());
            mGenresLayout.removeAllViews();

            if (movie.getDirector() != null) {
                mDirectorTv.setText(String.format(Locale.getDefault(), "Director: %s", movie.getDirector()));
            }

            int _16dp = (int) itemView.getResources().getDimension(R.dimen.full_padding);
            int _8dp = (int) itemView.getResources().getDimension(R.dimen.half_padding);
            float corner = itemView.getResources().getDimension(R.dimen.genre_corner);

            if (movie.getPaletteColors() != null) {
                movieTitleTV.setTextColor(movie.getPaletteColors().getTitleColor());
                mOverviewLabelTV.setTextColor(movie.getPaletteColors().getTitleColor());
                mTaglineTV.setTextColor(movie.getPaletteColors().getTextColor());
                mRuntimeTV.setTextColor(movie.getPaletteColors().getTextColor());
                movieYearTV.setTextColor(movie.getPaletteColors().getTextColor());
                movieOverview.setTextColor(movie.getPaletteColors().getTextColor());
                mDirectorTv.setTextColor(movie.getPaletteColors().getTextColor());
                int primaryDarkColor = movie.getPaletteColors().getStatusBarColor();

                // Adds each genre to the genre layout
                for (Genre genre : movie.getGenres()) {
                    TextView textView = new TextView(itemView.getContext());
                    textView.setText(genre.getName());
                    GradientDrawable shape = new GradientDrawable();
                    shape.setShape(GradientDrawable.RECTANGLE);
                    shape.setCornerRadius(corner);
                    shape.setColor(primaryDarkColor);
                    textView.setPadding(_8dp, _8dp, _8dp, _8dp);
                    textView.setBackground(shape);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    params.setMargins(0, 0, _16dp, 0);
                    textView.setLayoutParams(params);

                    mGenresLayout.addView(textView);
                }

            }


        }

    }
}
