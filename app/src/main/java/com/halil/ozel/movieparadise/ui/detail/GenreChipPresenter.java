package com.halil.ozel.movieparadise.ui.detail;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.ui.base.TvFocusHelper;

public class GenreChipPresenter extends Presenter {

    @Nullable
    private final DetailDescriptionPresenter.OnGenreClickListener genreClickListener;

    public GenreChipPresenter(@Nullable DetailDescriptionPresenter.OnGenreClickListener genreClickListener) {
        this.genreClickListener = genreClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView chip = new TextView(parent.getContext());
        int paddingH = parent.getResources().getDimensionPixelSize(R.dimen.full_padding);
        int paddingV = parent.getResources().getDimensionPixelSize(R.dimen.half_padding);
        chip.setPadding(paddingH, paddingV, paddingH, paddingV);
        chip.setTextColor(Color.WHITE);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        chip.setLayoutParams(new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TvFocusHelper.applyChip(chip);
        return new ViewHolder(chip);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Genre genre = (Genre) item;
        TextView chip = (TextView) viewHolder.view;
        chip.setText(genre.getName());
        if (genreClickListener != null) {
            chip.setOnClickListener(v -> genreClickListener.onGenreClicked(genre));
        } else {
            chip.setOnClickListener(null);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        viewHolder.view.setOnClickListener(null);
    }
}
