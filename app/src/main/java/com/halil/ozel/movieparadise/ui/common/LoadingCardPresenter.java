package com.halil.ozel.movieparadise.ui.common;

import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

public class LoadingCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        LoadingCardView cardView = new LoadingCardView(parent.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((LoadingCardView) viewHolder.view).startAnimation();
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ((LoadingCardView) viewHolder.view).stopAnimation();
    }
}
