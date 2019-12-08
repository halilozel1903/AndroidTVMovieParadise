package com.halil.ozel.movieparadise.ui.detail;

import android.support.v17.leanback.widget.DetailsOverviewLogoPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;
import android.view.ViewGroup;


public class CustomMovieDetailsPresenter extends FullWidthDetailsOverviewRowPresenter {

    private int previousState = STATE_FULL;

    public CustomMovieDetailsPresenter(Presenter detailsPresenter, DetailsOverviewLogoPresenter logoPresenter) {
        super(detailsPresenter, logoPresenter);
        setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_FULL);
    }

    @Override
    protected void onLayoutLogo(ViewHolder viewHolder, int oldState, boolean logoChanged) {
        View view = viewHolder.getLogoViewHolder().view;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        layoutParams.setMarginStart(view.getResources().getDimensionPixelSize(
                android.support.v17.leanback.R.dimen.lb_details_v2_logo_margin_start));
        layoutParams.topMargin = view.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_details_v2_blank_height) - layoutParams.height / 2;

        float offset = view.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_details_v2_actions_height) + view
                .getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_details_v2_description_margin_top) + (layoutParams.height / 2);

        switch (viewHolder.getState()) {
            case STATE_FULL:
            default:
                if (previousState == STATE_HALF) {
                    view.animate().translationYBy(-offset);
                }

                break;
            case STATE_HALF:
                if (previousState == STATE_FULL) {
                    view.animate().translationYBy(offset);
                }

                break;
        }
        previousState = viewHolder.getState();
        view.setLayoutParams(layoutParams);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        FullWidthDetailsOverviewRowPresenter.ViewHolder viewHolder = (FullWidthDetailsOverviewRowPresenter.ViewHolder) holder;
        View view = viewHolder.getOverviewView();
        view.setBackgroundColor(getBackgroundColor());
        view.findViewById(android.support.v17.leanback.R.id.details_overview_actions_background)
                .setBackgroundColor(getActionsBackgroundColor());
    }
}
