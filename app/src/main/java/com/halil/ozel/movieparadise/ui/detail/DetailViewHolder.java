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

import java.text.NumberFormat;
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
    private final TextView mRatingRuntimeSeparatorTV;
    private final TextView mRuntimeYearSeparatorTV;
    private final LinearLayout mExtraInfoLayout;
    private final LinearLayout mGenresLayout;
    private final TextView mMoreDetailsTV;

    private final View itemView;
    private final DetailDescriptionPresenter.OnGenreClickListener genreClickListener;
    private MovieDetails boundMovie;

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
        mRatingRuntimeSeparatorTV = itemView.findViewById(R.id.rating_runtime_separator);
        mRuntimeYearSeparatorTV = itemView.findViewById(R.id.runtime_year_separator);
        mExtraInfoLayout = itemView.findViewById(R.id.extra_info);
        mGenresLayout   = itemView.findViewById(R.id.genres);
        mMoreDetailsTV = itemView.findViewById(R.id.more_details);
    }

    public void bind(MovieDetails movie) {
        if (movie == null) return;
        boundMovie = movie;

        // Title fallbacks: title -> originalTitle -> empty
        String title = movie.getTitle() != null ? movie.getTitle() : (movie.getOriginalTitle() != null ? movie.getOriginalTitle() : "");
        movieTitleTV.setText(title);

        // Runtime might be 0 if unknown; avoid showing it as a real duration.
        boolean hasRuntime = movie.getRuntime() > 0;
        if (movie.getRuntime() > 0) {
            mRuntimeTV.setText(String.format(Locale.getDefault(), "%d min", movie.getRuntime()));
        } else {
            mRuntimeTV.setText("");
        }
        mRuntimeTV.setVisibility(hasRuntime ? View.VISIBLE : View.GONE);

        setTextOrGone(mTaglineTV, movie.getTagline());

        // Safe release year extraction
        String releaseDate = movie.getReleaseDate();
        boolean hasYear = releaseDate != null && releaseDate.length() >= 4;
        if (releaseDate != null && releaseDate.length() >= 4) {
            movieYearTV.setText(String.format(Locale.getDefault(), "(%s)", releaseDate.substring(0, 4)));
        } else {
            movieYearTV.setText("");
        }
        movieYearTV.setVisibility(hasYear ? View.VISIBLE : View.GONE);

        // Rating may be 0 for unreleased or unrated movies.
        boolean hasRating = movie.getVoteAverage() > 0f;
        if (movie.getVoteAverage() > 0f) {
            mRatingTv.setText(String.format(Locale.getDefault(), "⭐ %.1f / 10", movie.getVoteAverage()));
        } else {
            mRatingTv.setText("");
        }
        mRatingTv.setVisibility(hasRating ? View.VISIBLE : View.GONE);
        mRatingRuntimeSeparatorTV.setVisibility(hasRating && hasRuntime ? View.VISIBLE : View.GONE);
        mRuntimeYearSeparatorTV.setVisibility(hasYear && (hasRating || hasRuntime) ? View.VISIBLE : View.GONE);

        boolean hasOverview = hasText(movie.getOverview());
        movieOverview.setText(hasOverview ? movie.getOverview() : "");
        movieOverview.setVisibility(hasOverview ? View.VISIBLE : View.GONE);
        mOverviewLabelTV.setVisibility(hasOverview ? View.VISIBLE : View.GONE);

        bindExtraInfo(movie, title);
        mGenresLayout.removeAllViews();

        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            mDirectorTv.setText(String.format(Locale.getDefault(), "Director: %s", movie.getDirector()));
            mDirectorTv.setVisibility(View.VISIBLE);
        } else {
            mDirectorTv.setVisibility(View.GONE);
        }

        if (movie.getGenres() != null) {
            for (Genre genre : movie.getGenres()) {
                mGenresLayout.addView(createGenreBadge(genre));
            }
        }
        mGenresLayout.setVisibility(mGenresLayout.getChildCount() > 0 ? View.VISIBLE : View.GONE);
    }

    private void bindExtraInfo(MovieDetails movie, String title) {
        mExtraInfoLayout.removeAllViews();
        addExtraInfo("Original Title", differentText(movie.getOriginalTitle(), title));
        addExtraInfo("Language", formatLanguage(movie.getOriginalLanguage()));
        addExtraInfo("Status", movie.getStatus());
        addExtraInfo("IMDb", movie.getImdbId());
        addExtraInfo("Vote Count", movie.getVoteCount() > 0
                ? NumberFormat.getIntegerInstance(Locale.getDefault()).format(movie.getVoteCount())
                : null);
        addExtraInfo("Popularity", movie.getPopularity() > 0f
                ? String.format(Locale.getDefault(), "%.1f", movie.getPopularity())
                : null);
        addExtraInfo("Budget", movie.getBudget() > 0 ? formatMoney(movie.getBudget()) : null);
        addExtraInfo("Revenue", movie.getRevenue() > 0 ? formatMoney(movie.getRevenue()) : null);

        boolean hasExtraInfo = mExtraInfoLayout.getChildCount() > 0;
        boolean hasLongOverview = hasText(movie.getOverview()) && movie.getOverview().length() > 180;
        mMoreDetailsTV.setVisibility(hasExtraInfo || hasLongOverview ? View.VISIBLE : View.GONE);
        mMoreDetailsTV.setBackground(createInfoButtonBackground(false));
        mMoreDetailsTV.setOnClickListener(v -> toggleExtraInfo(hasExtraInfo));
        mMoreDetailsTV.setOnFocusChangeListener((v, hasFocus) ->
                mMoreDetailsTV.setBackground(createInfoButtonBackground(hasFocus)));
        applyExpandedState(movie.isDetailsExpanded(), hasExtraInfo);
    }

    private void toggleExtraInfo(boolean hasExtraInfo) {
        if (boundMovie == null) {
            return;
        }
        boolean expanded = !boundMovie.isDetailsExpanded();
        boundMovie.setDetailsExpanded(expanded);
        applyExpandedState(expanded, hasExtraInfo);
    }

    private void applyExpandedState(boolean expanded, boolean hasExtraInfo) {
        movieOverview.setMaxLines(expanded ? Integer.MAX_VALUE : 5);
        movieOverview.setEllipsize(expanded ? null : android.text.TextUtils.TruncateAt.END);
        mExtraInfoLayout.setVisibility(expanded && hasExtraInfo ? View.VISIBLE : View.GONE);
        mMoreDetailsTV.setText(expanded ? R.string.less_details : R.string.more_details);
    }

    private void addExtraInfo(String label, String value) {
        if (!hasText(value)) {
            return;
        }
        TextView textView = new TextView(itemView.getContext());
        textView.setText(String.format(Locale.getDefault(), "%s: %s", label, value.trim()));
        textView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
        textView.setTextSize(12f);
        textView.setSingleLine(true);
        textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) itemView.getResources().getDimension(R.dimen.half_padding));
        textView.setLayoutParams(params);
        mExtraInfoLayout.addView(textView);
    }

    private void setTextOrGone(TextView textView, String value) {
        if (hasText(value)) {
            textView.setText(value);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setText("");
            textView.setVisibility(View.GONE);
        }
    }

    private String differentText(String value, String currentTitle) {
        if (!hasText(value) || value.trim().equalsIgnoreCase(currentTitle == null ? "" : currentTitle.trim())) {
            return null;
        }
        return value;
    }

    private String formatLanguage(String language) {
        if (!hasText(language)) {
            return null;
        }
        Locale locale = Locale.forLanguageTag(language);
        String displayLanguage = locale.getDisplayLanguage(Locale.getDefault());
        if (!hasText(displayLanguage)) {
            return language.toUpperCase(Locale.getDefault());
        }
        return displayLanguage;
    }

    private String formatMoney(int amount) {
        return "$" + NumberFormat.getIntegerInstance(Locale.US).format(amount);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private TextView createGenreBadge(Genre genre) {
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
        badge.setBackground(createGenreBackground(false));
        badge.setOnClickListener(v -> {
            if (genreClickListener != null) {
                genreClickListener.onGenreClicked(genre);
            }
        });
        badge.setOnFocusChangeListener((v, hasFocus) -> {
            v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
            v.setBackground(createGenreBackground(hasFocus));
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, paddingV, 0);
        badge.setLayoutParams(params);
        return badge;
    }

    private GradientDrawable createGenreBackground(boolean focused) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(itemView.getResources().getDimension(R.dimen.genre_corner));
        shape.setColor(ContextCompat.getColor(
                itemView.getContext(),
                focused ? R.color.accent_color : R.color.genre_chip_background));
        shape.setStroke(
                focused ? 2 : 1,
                ContextCompat.getColor(
                        itemView.getContext(),
                        focused ? R.color.white : R.color.details_surface_stroke));
        return shape;
    }

    private GradientDrawable createInfoButtonBackground(boolean focused) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(itemView.getResources().getDimension(R.dimen.genre_corner));
        shape.setColor(ContextCompat.getColor(
                itemView.getContext(),
                focused ? R.color.accent_color : R.color.genre_chip_background));
        shape.setStroke(
                focused ? 2 : 1,
                ContextCompat.getColor(itemView.getContext(), R.color.details_surface_stroke));
        return shape;
    }
}
