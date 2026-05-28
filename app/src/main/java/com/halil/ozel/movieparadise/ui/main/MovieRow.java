package com.halil.ozel.movieparadise.ui.main;

import androidx.leanback.widget.ArrayObjectAdapter;

import java.util.HashSet;
import java.util.Set;


public class MovieRow {
    private int page;
    private int id;
    private ArrayObjectAdapter adapter;
    private String title;
    private boolean loading;
    private boolean exhausted;
    private final Set<String> itemKeys = new HashSet<>();

    public MovieRow() {}

    public int getPage() {
        return page;
    }

    public MovieRow setPage(int page) {
        this.page = page;
        return this;
    }

    public int getId() {
        return id;
    }

    public MovieRow setId(int id) {
        this.id = id;
        return this;
    }

    public ArrayObjectAdapter getAdapter() {
        return adapter;
    }

    public MovieRow setAdapter(ArrayObjectAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MovieRow setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isLoading() {
        return loading;
    }

    public MovieRow setLoading(boolean loading) {
        this.loading = loading;
        return this;
    }

    public boolean isExhausted() {
        return exhausted;
    }

    public MovieRow setExhausted(boolean exhausted) {
        this.exhausted = exhausted;
        return this;
    }

    public boolean addKeyIfAbsent(String key) {
        return key != null && itemKeys.add(key);
    }
}
