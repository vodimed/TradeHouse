package com.expertek.tradehouse.documents;

import android.database.sqlite.SQLiteDatabase;

import com.expertek.tradehouse.documents.sqlite.*;

import java.io.File;

public class Documents_v1Sqlite implements DBDocuments {
    private final SQLiteDatabase db;
    private final Documents documents;
    private final Lines lines;
    private final Marklines marklines;

    public Documents_v1Sqlite(String name) {
        db = SQLiteDatabase.openOrCreateDatabase(new File(name), null);
        documents = new Documents(db);
        lines = new Lines(db);
        marklines = new Marklines(db);
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
