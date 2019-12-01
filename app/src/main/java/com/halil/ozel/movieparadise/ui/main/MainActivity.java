package com.halil.ozel.movieparadise.ui.main;

import android.os.Bundle;

import com.halil.ozel.movieparadise.ui.base.BaseTvActivity;



public class MainActivity extends BaseTvActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(MainFragment.newInstance());
    }
}
