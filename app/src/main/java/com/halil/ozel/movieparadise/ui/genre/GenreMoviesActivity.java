package com.halil.ozel.movieparadise.ui.genre;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.os.BundleCompat;

import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.ui.base.BaseTVActivity;

public class GenreMoviesActivity extends BaseTVActivity {

    private static final String EXTRA_GENRE = "extra_genre";

    public static Intent newIntent(Context context, Genre genre) {
        Intent intent = new Intent(context, GenreMoviesActivity.class);
        intent.putExtra(EXTRA_GENRE, genre);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        Genre genre = extras == null
                ? null
                : BundleCompat.getParcelable(extras, EXTRA_GENRE, Genre.class);

        addFragment(GenreMoviesFragment.newInstance(genre));
    }
}
