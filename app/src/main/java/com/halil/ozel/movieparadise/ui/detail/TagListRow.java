package com.halil.ozel.movieparadise.ui.detail;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

/**
 * Chip satırları — Leanback zoom yerine selector outline kullanır.
 */
public class TagListRow extends ListRow {

    public TagListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }
}
