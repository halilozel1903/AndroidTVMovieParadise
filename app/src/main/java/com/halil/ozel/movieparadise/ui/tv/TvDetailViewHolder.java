package com.halil.ozel.movieparadise.ui.tv;

import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.TvShow;

import java.text.NumberFormat;
import java.util.Locale;

/** Simple view holder used by {@link TvDetailDescriptionPresenter}. */
public class TvDetailViewHolder extends Presenter.ViewHolder {

    TextView titleTv;
    TextView yearTv;
    TextView overviewTv;
    TextView ratingTv;
    TextView taglineTv;
    TextView runtimeTv;
    TextView overviewLabelTv;
    TextView ratingRuntimeSeparatorTv;
    TextView runtimeYearSeparatorTv;
    TextView moreDetailsTv;
    LinearLayout genresLayout;
    LinearLayout extraInfoLayout;

    private final View itemView;
    private TvShow boundShow;
    private boolean boundHasExtraInfo;

    public TvDetailViewHolder(View view) {
        super(view);
        itemView = view;
        titleTv = itemView.findViewById(R.id.movie_title);
        yearTv = itemView.findViewById(R.id.movie_year);
        overviewTv = itemView.findViewById(R.id.overview);
        ratingTv = itemView.findViewById(R.id.rating);
        taglineTv = itemView.findViewById(R.id.tagline);
        runtimeTv = itemView.findViewById(R.id.runtime);
        overviewLabelTv = itemView.findViewById(R.id.overview_label);
        ratingRuntimeSeparatorTv = itemView.findViewById(R.id.rating_runtime_separator);
        runtimeYearSeparatorTv = itemView.findViewById(R.id.runtime_year_separator);
        moreDetailsTv = itemView.findViewById(R.id.more_details);
        genresLayout = itemView.findViewById(R.id.genres);
        extraInfoLayout = itemView.findViewById(R.id.extra_info);
    }

    public void bind(TvShow show) {
        if (show == null) {
            return;
        }
        boundShow = show;

        String title = hasText(show.getName()) ? show.getName() : show.getOriginalName();
        titleTv.setText(hasText(title) ? title : "");
        setTextOrGone(taglineTv, show.getTagline());

        boolean hasRating = show.getVoteAverage() > 0f;
        ratingTv.setText(hasRating
                ? String.format(Locale.getDefault(), "⭐ %.1f / 10", show.getVoteAverage())
                : "");
        ratingTv.setVisibility(hasRating ? View.VISIBLE : View.GONE);

        Integer runtime = firstRuntime(show);
        boolean hasRuntime = runtime != null && runtime > 0;
        runtimeTv.setText(hasRuntime
                ? String.format(Locale.getDefault(), "%d min/ep", runtime)
                : "");
        runtimeTv.setVisibility(hasRuntime ? View.VISIBLE : View.GONE);

        String firstAirDate = show.getFirstAirDate();
        boolean hasYear = hasText(firstAirDate) && firstAirDate.length() >= 4;
        yearTv.setText(hasYear
                ? String.format(Locale.getDefault(), "(%s)", firstAirDate.substring(0, 4))
                : "");
        yearTv.setVisibility(hasYear ? View.VISIBLE : View.GONE);
        ratingRuntimeSeparatorTv.setVisibility(hasRating && hasRuntime ? View.VISIBLE : View.GONE);
        runtimeYearSeparatorTv.setVisibility(hasYear && (hasRating || hasRuntime) ? View.VISIBLE : View.GONE);

        boolean hasOverview = hasText(show.getOverview());
        overviewTv.setText(hasOverview ? show.getOverview() : "");
        overviewTv.setVisibility(hasOverview ? View.VISIBLE : View.GONE);
        overviewLabelTv.setVisibility(hasOverview ? View.VISIBLE : View.GONE);

        genresLayout.removeAllViews();
        if (show.getGenres() != null) {
            for (Genre genre : show.getGenres()) {
                genresLayout.addView(createGenreBadge(genre));
            }
        }
        genresLayout.setVisibility(genresLayout.getChildCount() > 0 ? View.VISIBLE : View.GONE);

        bindExtraInfo(show, title);
    }

    private void bindExtraInfo(TvShow show, String title) {
        extraInfoLayout.removeAllViews();
        addExtraInfo("Original Name", differentText(show.getOriginalName(), title));
        addExtraInfo("Language", formatLanguage(show.getOriginalLanguage()));
        addExtraInfo("Status", show.getStatus());
        addExtraInfo("Type", show.getType());
        addExtraInfo("First Air", show.getFirstAirDate());
        addExtraInfo("Last Air", show.getLastAirDate());
        addExtraInfo("Seasons", show.getNumberOfSeasons() > 0
                ? String.valueOf(show.getNumberOfSeasons())
                : null);
        addExtraInfo("Episodes", show.getNumberOfEpisodes() > 0
                ? String.valueOf(show.getNumberOfEpisodes())
                : null);
        addExtraInfo("Vote Count", show.getVoteCount() > 0
                ? NumberFormat.getIntegerInstance(Locale.getDefault()).format(show.getVoteCount())
                : null);
        addExtraInfo("Popularity", show.getPopularity() > 0f
                ? String.format(Locale.getDefault(), "%.1f", show.getPopularity())
                : null);
        addExtraInfo("Country", show.getOriginCountry() != null && !show.getOriginCountry().isEmpty()
                ? TextUtils.join(", ", show.getOriginCountry())
                : null);

        boundHasExtraInfo = extraInfoLayout.getChildCount() > 0;
        boolean hasLongOverview = hasText(show.getOverview()) && show.getOverview().length() > 180;
        moreDetailsTv.setVisibility(boundHasExtraInfo || hasLongOverview ? View.VISIBLE : View.GONE);
        moreDetailsTv.setBackground(createButtonBackground(false));
        moreDetailsTv.setOnClickListener(v -> toggleExtraInfo());
        moreDetailsTv.setOnFocusChangeListener((v, hasFocus) ->
                moreDetailsTv.setBackground(createButtonBackground(hasFocus)));
        applyExpandedState(show.isDetailsExpanded());
    }

    private void toggleExtraInfo() {
        if (boundShow == null) {
            return;
        }
        boolean expanded = !boundShow.isDetailsExpanded();
        boundShow.setDetailsExpanded(expanded);
        applyExpandedState(expanded);
    }

    private void applyExpandedState(boolean expanded) {
        overviewTv.setMaxLines(expanded ? Integer.MAX_VALUE : 5);
        overviewTv.setEllipsize(expanded ? null : TextUtils.TruncateAt.END);
        extraInfoLayout.setVisibility(expanded && boundHasExtraInfo ? View.VISIBLE : View.GONE);
        moreDetailsTv.setText(expanded ? R.string.less_details : R.string.more_details);
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
        textView.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) itemView.getResources().getDimension(R.dimen.half_padding));
        textView.setLayoutParams(params);
        extraInfoLayout.addView(textView);
    }

    private TextView createGenreBadge(Genre genre) {
        int paddingH = (int) itemView.getResources().getDimension(R.dimen.full_padding);
        int paddingV = (int) itemView.getResources().getDimension(R.dimen.half_padding);

        TextView badge = new TextView(itemView.getContext());
        badge.setText(genre.getName());
        badge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
        badge.setTextSize(12f);
        badge.setSingleLine(true);
        badge.setPadding(paddingH, paddingV, paddingH, paddingV);
        badge.setBackground(createChipBackground(false));
        badge.setOnFocusChangeListener((v, hasFocus) -> v.setBackground(createChipBackground(hasFocus)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, paddingV, 0);
        badge.setLayoutParams(params);
        return badge;
    }

    private GradientDrawable createChipBackground(boolean focused) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(itemView.getResources().getDimension(R.dimen.genre_corner));
        shape.setColor(ContextCompat.getColor(
                itemView.getContext(),
                focused ? R.color.accent_color : R.color.genre_chip_background));
        shape.setStroke(1, ContextCompat.getColor(itemView.getContext(), R.color.details_surface_stroke));
        return shape;
    }

    private GradientDrawable createButtonBackground(boolean focused) {
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

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
