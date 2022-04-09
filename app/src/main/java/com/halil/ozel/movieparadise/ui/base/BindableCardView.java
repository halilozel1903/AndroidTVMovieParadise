package com.halil.ozel.movieparadise.ui.base;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.leanback.widget.BaseCardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;


public abstract class BindableCardView<T> extends BaseCardView {

    public BindableCardView(Context context) {
        super(context);
        initLayout();
    }

    public BindableCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public BindableCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    private void initLayout() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(getLayoutResource(), this);
    }

    protected abstract void bind(T data);
    protected abstract @LayoutRes
    int getLayoutResource();
}
