package com.halil.ozel.movieparadise.ui.detail;

import android.view.View;
import android.widget.TextView;

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

    public DetailViewHolder(View view) {
        super(view);
        titleTv = view.findViewById(R.id.movie_title);
        yearTv = view.findViewById(R.id.movie_year);
        overviewTv = view.findViewById(R.id.overview);
        runtimeTv = view.findViewById(R.id.runtime);
        taglineTv = view.findViewById(R.id.tagline);
        ratingTv = view.findViewById(R.id.rating);
        directorTv = view.findViewById(R.id.director_tv);
        overviewLabelTv = view.findViewById(R.id.overview_label);
        ratingRuntimeSeparatorTv = view.findViewById(R.id.rating_runtime_separator);
        runtimeYearSeparatorTv = view.findViewById(R.id.runtime_year_separator);
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
        overviewTv.setEllipsize(android.text.TextUtils.TruncateAt.END);
        overviewTv.setVisibility(hasOverview ? View.VISIBLE : View.GONE);
        overviewLabelTv.setVisibility(hasOverview ? View.VISIBLE : View.GONE);
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
}
