package com.expertek.tradehouse.documents;

import androidx.annotation.NonNull;

import com.common.extensions.database.SQLiteDatabase;
import com.expertek.tradehouse.documents.sqlite.Documents;
import com.expertek.tradehouse.documents.sqlite.Lines;
import com.expertek.tradehouse.documents.sqlite.Marklines;

public class Documents_v1Sqlite extends SQLiteDatabase implements DBDocuments {
    private final Documents documents;
    private final Lines lines;
    private final Marklines marklines;

    public Documents_v1Sqlite(@NonNull String path) {
        super(path, 1);
        documents = new Documents(this);
        lines = new Lines(this);
        marklines = new Marklines(this);
    }

    public Documents documents() {
        return documents;
    }

    public Lines lines() {
        return lines;
    }

    public Marklines marklines() {
        return marklines;
    }
}
