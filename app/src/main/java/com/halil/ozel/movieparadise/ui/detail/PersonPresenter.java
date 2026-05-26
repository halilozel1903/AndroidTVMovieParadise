package com.halil.ozel.movieparadise.ui.detail;

import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.data.models.CastMember;

public class PersonPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(new PersonCardView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((PersonCardView) viewHolder.view).bind((CastMember) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ((PersonCardView) viewHolder.view).unbind();
    }
}
