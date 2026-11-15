package com.halil.ozel.movieparadise.ui.detail;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

/**
 * Chip rows: use a selector outline instead of the Leanback zoom highlight.
 */
public class TagListRow extends ListRow {

    public TagListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }
}
