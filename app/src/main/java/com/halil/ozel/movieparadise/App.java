package com.halil.ozel.movieparadise;

import android.app.Application;

import com.halil.ozel.movieparadise.dagger.components.ApplicationComponent;
import com.halil.ozel.movieparadise.dagger.components.DaggerApplicationComponent;
import com.halil.ozel.movieparadise.dagger.modules.ApplicationModule;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;

public class App extends Application {

    private static App instance;
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Creates Dagger Graph
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpClientModule(new HttpClientModule())
                .build();

        applicationComponent.inject(this);
    }

    public static App instance() {
        return instance;
    }

    public ApplicationComponent appComponent() {
        return applicationComponent;
    }
}
