package com.halil.ozel.movieparadise.ui.common;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.ui.base.TvFocusHelper;

public class StateCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_state, parent, false);
        view.setDefaultFocusHighlightEnabled(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        View card = viewHolder.view;
        TextView messageView = card.findViewById(R.id.state_message_tv);
        TextView actionView = card.findViewById(R.id.state_action_tv);

        String message = null;
        Runnable action = null;
        String actionLabel = null;
        boolean showAction = false;

        if (item instanceof UiStateItem.Error) {
            UiStateItem.Error error = (UiStateItem.Error) item;
            message = error.getMessage();
            action = error.getRetryAction();
            actionLabel = actionView.getContext().getString(R.string.retry_action);
            showAction = action != null;
        } else if (item instanceof UiStateItem.Retry) {
            UiStateItem.Retry retry = (UiStateItem.Retry) item;
            message = retry.getMessage();
            action = retry.getRetryAction();
            actionLabel = actionView.getContext().getString(R.string.retry_action);
            showAction = action != null;
        } else if (item instanceof UiStateItem.Empty) {
            message = ((UiStateItem.Empty) item).getMessage();
        }

        messageView.setText(message);
        card.setOnClickListener(null);
        card.setOnKeyListener(null);

        if (showAction) {
            actionView.setVisibility(View.VISIBLE);
            actionView.setText(actionLabel);
            TvFocusHelper.styleActionLabel(actionView);
            Runnable confirmAction = action;
            card.setOnClickListener(v -> runAction(confirmAction));
            card.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == KeyEvent.KEYCODE_ENTER
                        || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    runAction(confirmAction);
                    return true;
                }
                return false;
            });
        } else {
            actionView.setVisibility(View.GONE);
        }
    }

    private void runAction(Runnable action) {
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        viewHolder.view.setOnClickListener(null);
        viewHolder.view.setOnKeyListener(null);
    }
}
