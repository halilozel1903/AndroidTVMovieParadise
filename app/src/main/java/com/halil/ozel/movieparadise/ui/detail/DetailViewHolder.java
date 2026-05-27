package com.halil.ozel.movieparadise.ui.detail;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.MovieDetails;
import com.halil.ozel.movieparadise.data.models.TvShow;

import java.util.List;
import java.util.Locale;

public class DetailViewHolder extends Presenter.ViewHolder {

    private final TextView titleTv;
    private final TextView yearTv;
    private final TextView overviewTv;
    private final TextView runtimeTv;
    private final TextView taglineTv;
    private final TextView ratingTv;
    private final TextView directorTv;
    private final TextView overviewLabelTv;
    private final TextView ratingRuntimeSeparatorTv;
    private final TextView runtimeYearSeparatorTv;
    private final LinearLayout genresLayout;

    private final View itemView;
    private final DetailDescriptionPresenter.OnGenreClickListener genreClickListener;

    public DetailViewHolder(View view,
                            DetailDescriptionPresenter.OnGenreClickListener genreClickListener) {
        super(view);
        itemView = view;
        this.genreClickListener = genreClickListener;
        titleTv = itemView.findViewById(R.id.movie_title);
        yearTv = itemView.findViewById(R.id.movie_year);
        overviewTv = itemView.findViewById(R.id.overview);
        runtimeTv = itemView.findViewById(R.id.runtime);
        taglineTv = itemView.findViewById(R.id.tagline);
        ratingTv = itemView.findViewById(R.id.rating);
        directorTv = itemView.findViewById(R.id.director_tv);
        overviewLabelTv = itemView.findViewById(R.id.overview_label);
        ratingRuntimeSeparatorTv = itemView.findViewById(R.id.rating_runtime_separator);
        runtimeYearSeparatorTv = itemView.findViewById(R.id.runtime_year_separator);
        genresLayout = itemView.findViewById(R.id.genres);
    }

    public void bind(Object item) {
        if (item instanceof MovieDetails) {
            bindMovie((MovieDetails) item);
        } else if (item instanceof TvShow) {
            bindTvShow((TvShow) item);
        }
    }

    private void bindMovie(MovieDetails movie) {
        String title = hasText(movie.getTitle()) ? movie.getTitle() : movie.getOriginalTitle();
        bindCommon(
                title,
                movie.getOverview(),
                movie.getTagline(),
                movie.getReleaseDate(),
                movie.getRuntime() > 0 ? String.format(Locale.getDefault(), "%d min", movie.getRuntime()) : null,
                movie.getVoteAverage(),
                movie.getGenres());

        if (hasText(movie.getDirector())) {
            directorTv.setText(String.format(Locale.getDefault(), "Director: %s", movie.getDirector()));
            directorTv.setVisibility(View.VISIBLE);
        } else {
            directorTv.setText("");
            directorTv.setVisibility(View.GONE);
        }
    }

    private void bindTvShow(TvShow show) {
        String title = hasText(show.getName()) ? show.getName() : show.getOriginalName();
        Integer runtime = firstRuntime(show);
        bindCommon(
                title,
                show.getOverview(),
                show.getTagline(),
                show.getFirstAirDate(),
                runtime != null && runtime > 0
                        ? String.format(Locale.getDefault(), "%d min/ep", runtime)
                        : null,
                show.getVoteAverage(),
                show.getGenres());
        directorTv.setText("");
        directorTv.setVisibility(View.GONE);
    }

    private void bindCommon(String title,
                            String overview,
                            String tagline,
                            String date,
                            String runtime,
                            float rating,
                            List<Genre> genres) {
        titleTv.setText(hasText(title) ? title : "");
        setTextOrGone(taglineTv, tagline);

        boolean hasRating = rating > 0f;
        ratingTv.setText(hasRating
                ? String.format(Locale.getDefault(), "⭐ %.1f / 10", rating)
                : "");
        ratingTv.setVisibility(hasRating ? View.VISIBLE : View.GONE);

        boolean hasRuntime = hasText(runtime);
        runtimeTv.setText(hasRuntime ? runtime : "");
        runtimeTv.setVisibility(hasRuntime ? View.VISIBLE : View.GONE);

        boolean hasYear = hasText(date) && date.length() >= 4;
        yearTv.setText(hasYear
                ? String.format(Locale.getDefault(), "(%s)", date.substring(0, 4))
                : "");
        yearTv.setVisibility(hasYear ? View.VISIBLE : View.GONE);
        ratingRuntimeSeparatorTv.setVisibility(hasRating && hasRuntime ? View.VISIBLE : View.GONE);
        runtimeYearSeparatorTv.setVisibility(hasYear && (hasRating || hasRuntime) ? View.VISIBLE : View.GONE);

        boolean hasOverview = hasText(overview);
        overviewTv.setText(hasOverview ? overview : "");
        overviewTv.setMaxLines(7);
        overviewTv.setEllipsize(TextUtils.TruncateAt.END);
        overviewTv.setVisibility(hasOverview ? View.VISIBLE : View.GONE);
        overviewLabelTv.setVisibility(hasOverview ? View.VISIBLE : View.GONE);

        genresLayout.removeAllViews();
        if (genres != null) {
            for (Genre genre : genres) {
                if (genre != null && hasText(genre.getName())) {
                    genresLayout.addView(createGenreBadge(genre));
                }
            }
        }
        genresLayout.setVisibility(genresLayout.getChildCount() > 0 ? View.VISIBLE : View.GONE);
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

    private Integer firstRuntime(TvShow show) {
        if (show.getEpisodeRunTime() == null || show.getEpisodeRunTime().isEmpty()) {
            return null;
        }
        return show.getEpisodeRunTime().get(0);
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
        badge.setFocusable(genreClickListener != null);
        badge.setClickable(genreClickListener != null);
        badge.setPadding(paddingH, paddingV, paddingH, paddingV);
        badge.setBackground(createGenreBackground(false));
        badge.setOnClickListener(v -> {
            if (genreClickListener != null) {
                genreClickListener.onGenreClicked(genre);
            }
        });
        badge.setOnFocusChangeListener((v, hasFocus) ->
                v.setBackground(createGenreBackground(hasFocus)));

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
}
