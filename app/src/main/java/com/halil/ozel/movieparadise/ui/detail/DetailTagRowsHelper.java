package com.halil.ozel.movieparadise.ui.detail;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;

import com.halil.ozel.movieparadise.R;
import com.halil.ozel.movieparadise.data.models.Genre;
import com.halil.ozel.movieparadise.data.models.WatchProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Detay ekranında tür ve platform chip'lerini odaklanabilir yatay satırlara taşır.
 */
public class DetailTagRowsHelper {

    private static final long HEADER_GENRES = 100L;
    private static final long HEADER_PROVIDERS = 101L;

    private ListRow genreRow;
    private ListRow providerRow;
    private final ArrayObjectAdapter genreAdapter;
    private final ArrayObjectAdapter providerAdapter;
    @Nullable
    private List<Genre> lastGenres;
    @Nullable
    private List<WatchProvider> lastProviders;

    public DetailTagRowsHelper(@Nullable DetailDescriptionPresenter.OnGenreClickListener genreClickListener) {
        genreAdapter = new ArrayObjectAdapter(new GenreChipPresenter(genreClickListener));
        providerAdapter = new ArrayObjectAdapter(new WatchProviderChipPresenter());
    }

    public void updateGenres(ArrayObjectAdapter rowsAdapter, Context context, @Nullable List<Genre> genres) {
        if (sameGenres(lastGenres, genres)) {
            return;
        }
        lastGenres = copyGenres(genres);
        replaceAdapterItems(genreAdapter, buildGenreItems(genres));
        genreRow = syncRow(rowsAdapter, genreRow, genreAdapter, HEADER_GENRES,
                R.string.genres_label, context, null);
    }

    public void updateProviders(ArrayObjectAdapter rowsAdapter, Context context,
                                @Nullable List<WatchProvider> providers) {
        if (sameProviders(lastProviders, providers)) {
            return;
        }
        lastProviders = copyProviders(providers);
        replaceAdapterItems(providerAdapter, buildProviderItems(providers));
        ListRow anchor = genreRow != null && rowsAdapter.indexOf(genreRow) >= 0 ? genreRow : null;
        providerRow = syncRow(rowsAdapter, providerRow, providerAdapter, HEADER_PROVIDERS,
                R.string.watch_providers_label, context, anchor);
    }

    public int getCastInsertIndex(ArrayObjectAdapter rowsAdapter) {
        int index = 1;
        if (genreRow != null && rowsAdapter.indexOf(genreRow) >= 0) {
            index = rowsAdapter.indexOf(genreRow) + 1;
        }
        if (providerRow != null && rowsAdapter.indexOf(providerRow) >= 0) {
            index = rowsAdapter.indexOf(providerRow) + 1;
        }
        return index;
    }

    @Nullable
    private ListRow syncRow(ArrayObjectAdapter rowsAdapter,
                            @Nullable ListRow existingRow,
                            ArrayObjectAdapter contentAdapter,
                            long headerId,
                            @StringRes int headerRes,
                            Context context,
                            @Nullable ListRow insertAfter) {
        if (contentAdapter.size() == 0) {
            if (existingRow != null) {
                int index = rowsAdapter.indexOf(existingRow);
                if (index >= 0) {
                    rowsAdapter.removeItems(index, 1);
                }
            }
            return null;
        }

        if (existingRow == null) {
            ListRow row = new TagListRow(
                    new HeaderItem(headerId, context.getString(headerRes)),
                    contentAdapter);
            rowsAdapter.add(resolveInsertIndex(rowsAdapter, insertAfter), row);
            return row;
        }

        return existingRow;
    }

    private int resolveInsertIndex(ArrayObjectAdapter rowsAdapter, @Nullable ListRow insertAfter) {
        if (insertAfter == null) {
            return 1;
        }
        int anchorIndex = rowsAdapter.indexOf(insertAfter);
        return anchorIndex >= 0 ? anchorIndex + 1 : 1;
    }

    private static void replaceAdapterItems(ArrayObjectAdapter adapter, List<?> items) {
        adapter.clear();
        for (Object item : items) {
            adapter.add(item);
        }
    }

    private static List<Genre> buildGenreItems(@Nullable List<Genre> genres) {
        List<Genre> items = new ArrayList<>();
        if (genres == null) {
            return items;
        }
        for (Genre genre : genres) {
            if (genre != null && genre.getName() != null && !genre.getName().trim().isEmpty()) {
                items.add(genre);
            }
        }
        return items;
    }

    private static List<WatchProvider> buildProviderItems(@Nullable List<WatchProvider> providers) {
        List<WatchProvider> items = new ArrayList<>();
        if (providers == null) {
            return items;
        }
        for (WatchProvider provider : providers) {
            if (provider != null && provider.getLogoPath() != null) {
                items.add(provider);
            }
        }
        return items;
    }

    private static boolean sameGenres(@Nullable List<Genre> left, @Nullable List<Genre> right) {
        if (left == null || left.isEmpty()) {
            return right == null || right.isEmpty();
        }
        if (right == null || left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < left.size(); i++) {
            Genre a = left.get(i);
            Genre b = right.get(i);
            if (a == null || b == null || !Objects.equals(a.getId(), b.getId())) {
                return false;
            }
        }
        return true;
    }

    private static boolean sameProviders(@Nullable List<WatchProvider> left,
                                         @Nullable List<WatchProvider> right) {
        if (left == null || left.isEmpty()) {
            return right == null || right.isEmpty();
        }
        if (right == null || left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < left.size(); i++) {
            WatchProvider a = left.get(i);
            WatchProvider b = right.get(i);
            if (a == null || b == null || !Objects.equals(a.getProviderId(), b.getProviderId())) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static List<Genre> copyGenres(@Nullable List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return null;
        }
        return new ArrayList<>(genres);
    }

    @Nullable
    private static List<WatchProvider> copyProviders(@Nullable List<WatchProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        return new ArrayList<>(providers);
    }
}
