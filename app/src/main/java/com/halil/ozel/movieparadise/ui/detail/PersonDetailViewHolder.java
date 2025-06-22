package com.halil.ozel.movieparadise.ui.detail;

import android.view.View;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Person;

public class PersonDetailViewHolder extends Presenter.ViewHolder {

    TextView nameTv;
    TextView birthTv;
    TextView bioTv;

    public PersonDetailViewHolder(View view) {
        super(view);
        nameTv = view.findViewById(R.id.person_name);
        birthTv = view.findViewById(R.id.person_birth);
        bioTv = view.findViewById(R.id.person_bio);
    }

    public void bind(Person person) {
        if (person != null) {
            nameTv.setText(person.getName());
            String born = "";
            if (person.getBirthday() != null) {
                born += person.getBirthday();
            }
            if (person.getPlaceOfBirth() != null) {
                if (!born.isEmpty()) born += " - ";
                born += person.getPlaceOfBirth();
            }
            birthTv.setText(born);
            bioTv.setText(person.getBiography());
        }
    }
}
