package com.halil.ozel.movieparadise.ui.detail;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.WatchProvider;
import com.halil.ozel.movieparadise.ui.base.GlideUtils;
import com.halil.ozel.movieparadise.ui.base.TvFocusHelper;

public class WatchProviderChipPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        int size = parent.getResources().getDimensionPixelSize(R.dimen.provider_icon_size);
        int padding = parent.getResources().getDimensionPixelSize(R.dimen.half_padding);

        FrameLayout container = new FrameLayout(parent.getContext());
        ViewGroup.MarginLayoutParams containerParams = new ViewGroup.MarginLayoutParams(
                size + padding * 2,
                size + padding * 2);
        containerParams.setMarginEnd(padding);
        container.setLayoutParams(containerParams);
        container.setPadding(padding, padding, padding, padding);
        TvFocusHelper.applyIconChip(container);

        ImageView icon = new ImageView(parent.getContext());
        icon.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setFocusable(false);
        icon.setId(R.id.provider_icon);
        container.addView(icon);

        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        WatchProvider provider = (WatchProvider) item;
        FrameLayout container = (FrameLayout) viewHolder.view;
        ImageView icon = container.findViewById(R.id.provider_icon);
        container.setContentDescription(provider.getProviderName());
        Glide.with(icon.getContext())
                .load(HttpClientModule.PROVIDER_LOGO_URL + provider.getLogoPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(icon);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageView icon = viewHolder.view.findViewById(R.id.provider_icon);
        if (icon != null) {
            GlideUtils.clearImageView(icon);
        }
    }
}
