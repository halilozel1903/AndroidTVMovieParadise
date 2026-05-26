package com.halil.ozel.movieparadise.ui.detail;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.MovieDetails;

import java.util.Locale;

public class DetailViewHolder extends Presenter.ViewHolder {

    private final TextView movieTitleTV;
    private final TextView movieYearTV;
    private final TextView movieOverview;
    private final TextView mRuntimeTV;
    private final TextView mTaglineTV;
    private final TextView mRatingTv;
    private final TextView mDirectorTv;
    private final TextView mOverviewLabelTV;
    private final LinearLayout mGenresLayout;

    private final View itemView;
    private final DetailDescriptionPresenter.OnGenreClickListener genreClickListener;

    public DetailViewHolder(View view,
                            DetailDescriptionPresenter.OnGenreClickListener genreClickListener) {
        super(view);
        itemView = view;
        this.genreClickListener = genreClickListener;
        movieTitleTV    = itemView.findViewById(R.id.movie_title);
        movieYearTV     = itemView.findViewById(R.id.movie_year);
        movieOverview   = itemView.findViewById(R.id.overview);
        mRuntimeTV      = itemView.findViewById(R.id.runtime);
        mTaglineTV      = itemView.findViewById(R.id.tagline);
        mRatingTv       = itemView.findViewById(R.id.rating);
        mDirectorTv     = itemView.findViewById(R.id.director_tv);
        mOverviewLabelTV= itemView.findViewById(R.id.overview_label);
        mGenresLayout   = itemView.findViewById(R.id.genres);
    }

    public void bind(MovieDetails movie) {
        if (movie == null || movie.getTitle() == null) return;

        mRuntimeTV.setText(
                String.format(Locale.getDefault(), "%d min", movie.getRuntime()));
        mTaglineTV.setText(movie.getTagline());
        movieTitleTV.setText(movie.getTitle());

        // Safe release year extraction
        String releaseDate = movie.getReleaseDate();
        if (releaseDate != null && releaseDate.length() >= 4) {
            movieYearTV.setText(String.format(Locale.getDefault(), "(%s)", releaseDate.substring(0, 4)));
        }

        // ⭐ rating prefix
        mRatingTv.setText(
                String.format(Locale.getDefault(), "⭐ %.1f / 10", movie.getVoteAverage()));
        movieOverview.setText(movie.getOverview());
        mGenresLayout.removeAllViews();

        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            mDirectorTv.setText(
                    String.format(Locale.getDefault(), "Director: %s", movie.getDirector()));
            mDirectorTv.setVisibility(View.VISIBLE);
        } else {
            mDirectorTv.setVisibility(View.GONE);
        }

        int badgeBgColor = ContextCompat.getColor(itemView.getContext(), R.color.accent_color);
        if (movie.getPaletteColors() != null) {
            badgeBgColor = movie.getPaletteColors().getStatusBarColor();
        }

        if (movie.getGenres() != null) {
            for (Genre genre : movie.getGenres()) {
                mGenresLayout.addView(createGenreBadge(genre, badgeBgColor));
            }
        }
    }

    private TextView createGenreBadge(Genre genre, int badgeBgColor) {
        int paddingH = (int) itemView.getResources().getDimension(R.dimen.full_padding);
        int paddingV = (int) itemView.getResources().getDimension(R.dimen.half_padding);

        TextView badge = new TextView(itemView.getContext());
        badge.setText(genre.getName());
        badge.setTextColor(Color.WHITE);
        badge.setTextSize(12f);
        badge.setSingleLine(true);
        badge.setFocusable(true);
        badge.setClickable(true);
        badge.setPadding(paddingH, paddingV, paddingH, paddingV);
        badge.setBackground(createGenreBackground(badgeBgColor, false));
        badge.setOnClickListener(v -> {
            if (genreClickListener != null) {
                genreClickListener.onGenreClicked(genre);
            }
        });
        badge.setOnFocusChangeListener((v, hasFocus) -> {
            v.animate().scaleX(hasFocus ? 1.04f : 1f).scaleY(hasFocus ? 1.04f : 1f).setDuration(120).start();
            v.setBackground(createGenreBackground(badgeBgColor, hasFocus));
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, paddingV, 0);
        badge.setLayoutParams(params);
        return badge;
    }

    private GradientDrawable createGenreBackground(int color, boolean focused) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(itemView.getResources().getDimension(R.dimen.genre_corner));
        shape.setColor(color);
        if (focused) {
            shape.setStroke(3, ContextCompat.getColor(itemView.getContext(), R.color.white));
        }
        return shape;
    }
}
