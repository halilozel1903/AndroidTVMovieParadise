package com.halil.ozel.movieparadise.ui.detail;

import androidx.leanback.widget.ArrayObjectAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationRowHelper<T> {

    public interface IdProvider<T> {
        String getId(T item);
    }

    public interface PosterProvider<T> {
        String getPosterPath(T item);
    }

    public interface ResultListener {
        void onRecommendationsReady();
        void onRecommendationsEmpty();
    }

    private final ArrayObjectAdapter adapter;
    private final IdProvider<T> idProvider;
    private final PosterProvider<T> posterProvider;
    private final ResultListener resultListener;
    private final Set<String> itemIds = new HashSet<>();
    private String currentId;
    private int pendingRequests;

    public RecommendationRowHelper(ArrayObjectAdapter adapter,
                                   IdProvider<T> idProvider,
                                   PosterProvider<T> posterProvider,
                                   ResultListener resultListener) {
        this.adapter = adapter;
        this.idProvider = idProvider;
        this.posterProvider = posterProvider;
        this.resultListener = resultListener;
    }

    public void cancel() {
        pendingRequests = 0;
        currentId = null;
        itemIds.clear();
    }

    public void start(String currentId, int requestCount) {
        cancel();
        this.currentId = currentId;
        pendingRequests = requestCount;
        adapter.clear();
    }

    public void append(List<T> items) {
        if (items != null) {
            for (T item : items) {
                if (canAdd(item)) {
                    adapter.add(item);
                }
            }
        }
        finishRequest();
    }

    public void finishRequest() {
        pendingRequests = Math.max(0, pendingRequests - 1);
        if (pendingRequests > 0) {
            return;
        }
        if (adapter.size() > 0) {
            resultListener.onRecommendationsReady();
        } else {
            resultListener.onRecommendationsEmpty();
        }
    }

    private boolean canAdd(T item) {
        if (item == null) {
            return false;
        }
        String id = idProvider.getId(item);
        return id != null
                && !id.equals(currentId)
                && posterProvider.getPosterPath(item) != null
                && itemIds.add(id);
    }
}
