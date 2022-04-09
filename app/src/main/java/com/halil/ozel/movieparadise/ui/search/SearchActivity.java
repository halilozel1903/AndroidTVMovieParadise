package com.halil.ozel.movieparadise.ui.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.leanback.app.SearchFragment;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;

public class SearchActivity extends BaseTVActivity {

    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(com.halil.ozel.movieparadise.ui.search.SearchFragment.newInstance());

        searchFragment = (SearchFragment) getFragmentManager().findFragmentById(R.id.search_fragment);

    }


    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }
}
