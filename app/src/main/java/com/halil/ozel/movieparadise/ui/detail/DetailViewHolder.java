package com.halil.ozel.movieparadise.ui.detail;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.MovieDetails;

import java.util.Locale;


public class DetailViewHolder extends Presenter.ViewHolder {

    TextView movieTitleTV;
    TextView movieYearTV;
    TextView movieOverview;
    TextView mRuntimeTV;
    TextView mTaglineTV;
    TextView mDirectorTv;
    TextView mOverviewLabelTV;
    LinearLayout mGenresLayout;

    private final View itemView;

    public DetailViewHolder(View view) {
        super(view);
        itemView = view;
        movieTitleTV = itemView.findViewById(R.id.movie_title);
        movieYearTV = itemView.findViewById(R.id.movie_year);
        movieOverview = itemView.findViewById(R.id.overview);
        mRuntimeTV = itemView.findViewById(R.id.runtime);
        mTaglineTV = itemView.findViewById(R.id.tagline);
        mDirectorTv = itemView.findViewById(R.id.director_tv);
        mOverviewLabelTV = itemView.findViewById(R.id.overview_label);
        mGenresLayout = itemView.findViewById(R.id.genres);
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
