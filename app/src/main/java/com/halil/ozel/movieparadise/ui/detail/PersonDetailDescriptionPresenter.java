package com.halil.ozel.movieparadise.ui.detail;

import androidx.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Person;

public class PersonDetailDescriptionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_person, parent, false);
        return new PersonDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Person person = (Person) item;
        PersonDetailViewHolder holder = (PersonDetailViewHolder) viewHolder;
        holder.bind(person);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {}
}
