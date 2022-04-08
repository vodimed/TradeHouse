package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.barcode;

import java.util.List;

public class Barcodes {
    private final SQLiteDatabase db;

    public Barcodes(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, barcode> load() {
        return new SQLitePager.Factory<>(db, barcode.class, "SELECT * FROM TH_barcodes");
    }

    public barcode get(String ident) {
        final DataSource<Integer, barcode> source = new SQLitePager.Factory<>(db, barcode.class,
                "SELECT * FROM TH_barcodes WHERE BC = :ident", ident).create();
        final List<barcode> result = ((SQLitePager<barcode>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
