package com.halil.ozel.movieparadise.ui.base;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class BaseRxPresenter {

    protected final CompositeDisposable disposables = new CompositeDisposable();

    public void clear() {
        disposables.clear();
    }
}
