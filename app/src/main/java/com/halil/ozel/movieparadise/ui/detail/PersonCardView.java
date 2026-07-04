package com.halil.ozel.movieparadise.ui.detail;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.dagger.modules.HttpClientModule;
import com.halil.ozel.movieparadise.data.models.CastMember;
import com.halil.ozel.movieparadise.ui.base.BindableCardView;
import com.halil.ozel.movieparadise.ui.base.GlideUtils;

public class PersonCardView extends BindableCardView<CastMember> {

    private final ImageView posterImageView;
    private final TextView titleTextView;
    private final TextView subtitleTextView;

    public PersonCardView(Context context) {
        super(context);
        posterImageView = findViewById(R.id.person_poster_iv);
        titleTextView = findViewById(R.id.person_title_tv);
        subtitleTextView = findViewById(R.id.person_subtitle_tv);
    }

    @Override
    protected void bind(CastMember castMember) {
        String profilePath = castMember.getProfilePath();
        Object posterModel = profilePath == null || profilePath.isEmpty()
                ? R.drawable.popcorn
                : HttpClientModule.PROFILE_URL + profilePath;

        Glide.with(getContext())
                .load(posterModel)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.popcorn)
                .error(R.drawable.popcorn)
                .fitCenter()
                .into(posterImageView);

        titleTextView.setText(castMember.getName());
        subtitleTextView.setText(castMember.getCharacter());
    }

    void unbind() {
        GlideUtils.clearImageView(posterImageView);
        titleTextView.setText(null);
        subtitleTextView.setText(null);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.card_person;
    }
}
