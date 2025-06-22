package com.halil.ozel.movieparadise.dagger.components;


import com.halil.ozel.movieparadise.App;
import com.halil.ozel.movieparadise.dagger.AppScope;
import com.halil.ozel.movieparadise.dagger.modules.ApplicationModule;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.ui.detail.DetailFragment;
import com.halil.ozel.movieparadise.ui.detail.PersonDetailFragment;
import com.halil.ozel.movieparadise.ui.main.MainFragment;
import com.halil.ozel.movieparadise.ui.tv.TvMainFragment;
import com.halil.ozel.movieparadise.ui.tv.TvDetailFragment;
import com.halil.ozel.movieparadise.ui.search.SearchFragment;

import javax.inject.Singleton;

import dagger.Component;

@AppScope
@Singleton
@Component(modules = {
        ApplicationModule.class,
        HttpClientModule.class,
})
public interface ApplicationComponent {

    void inject(App app);

    void inject(MainFragment mainFragment);

    void inject(TvMainFragment tvMainFragment);

    void inject(TvDetailFragment tvDetailFragment);

    void inject(DetailFragment movieDetailsFragment);

    void inject(SearchFragment searchFragment);

    void inject(PersonDetailFragment personDetailFragment);
}
