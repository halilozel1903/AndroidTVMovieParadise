package com.halil.ozel.movieparadise.ui.common;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

public class RowLoadingHelper {

    public static final int INITIAL_SKELETON_COUNT = 6;
    public static final int PAGINATION_SKELETON_COUNT = 2;

    private final LoadingCardPresenter loadingPresenter = new LoadingCardPresenter();
    private final StateCardPresenter statePresenter = new StateCardPresenter();
    private int loadingCount;

    public PresenterSelector createSelector(Presenter contentPresenter, Class<?> contentClass) {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        selector.addClassPresenter(contentClass, contentPresenter);
        selector.addClassPresenter(UiStateItem.Loading.class, loadingPresenter);
        selector.addClassPresenter(UiStateItem.Error.class, statePresenter);
        selector.addClassPresenter(UiStateItem.Retry.class, statePresenter);
        selector.addClassPresenter(UiStateItem.Empty.class, statePresenter);
        return selector;
    }

    public void showInitialLoading(ArrayObjectAdapter adapter, int count) {
        clearState(adapter);
        clearLoading(adapter);
        loadingCount = count;
        for (int i = 0; i < count; i++) {
            adapter.add(UiStateItem.Loading.get());
        }
    }

    public void showPaginationLoading(ArrayObjectAdapter adapter, int count) {
        if (loadingCount > 0) {
            return;
        }
        loadingCount = count;
        for (int i = 0; i < count; i++) {
            adapter.add(UiStateItem.Loading.get());
        }
    }

    public void clearLoading(ArrayObjectAdapter adapter) {
        if (loadingCount <= 0) {
            return;
        }
        int size = adapter.size();
        int removed = 0;
        for (int i = size - 1; i >= 0 && removed < loadingCount; i--) {
            if (adapter.get(i) instanceof UiStateItem.Loading) {
                adapter.removeItems(i, 1);
                removed++;
            }
        }
        loadingCount = 0;
    }

    public void clearState(ArrayObjectAdapter adapter) {
        for (int i = adapter.size() - 1; i >= 0; i--) {
            Object item = adapter.get(i);
            if (item instanceof UiStateItem.Error
                    || item instanceof UiStateItem.Retry
                    || item instanceof UiStateItem.Empty) {
                adapter.removeItems(i, 1);
            }
        }
    }

    public void showError(ArrayObjectAdapter adapter, String message, Runnable retryAction) {
        clearLoading(adapter);
        clearState(adapter);
        adapter.add(new UiStateItem.Error(message, retryAction));
    }

    public void showEmpty(ArrayObjectAdapter adapter, String message) {
        clearLoading(adapter);
        clearState(adapter);
        adapter.add(new UiStateItem.Empty(message));
    }

    public boolean hasLoadingItems() {
        return loadingCount > 0;
    }

    public int getContentSize(ArrayObjectAdapter adapter) {
        int count = 0;
        for (int i = 0; i < adapter.size(); i++) {
            Object item = adapter.get(i);
            if (!(item instanceof UiStateItem.Loading)
                    && !(item instanceof UiStateItem.Error)
                    && !(item instanceof UiStateItem.Retry)
                    && !(item instanceof UiStateItem.Empty)) {
                count++;
            }
        }
        return count;
    }
}
