package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.Barcode;

import java.util.List;

public class Barcodes {
    private final SQLiteDatabase db;

    public Barcodes(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Barcode> load(String ident) {
        return new SQLitePager.Factory<>(db, Barcode.class, "SELECT * FROM TH_barcodes " +
                "WHERE BC LIKE :ident || '%' OR :ident IS NULL", ident);
    }

    public Barcode get(String ident) {
        final DataSource<Integer, Barcode> source = new SQLitePager.Factory<>(db, Barcode.class,
                "SELECT * FROM TH_barcodes WHERE BC = :ident", ident).create();
        final List<Barcode> result = ((SQLitePager<Barcode>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public double getRate(String ident) {
        final SQLiteStatement stmt = db.compileStatement(
                "SELECT UnitRate * 1000000 FROM TH_barcodes WHERE BC = :ident");
        stmt.bindString(1, ident);
        try {
            return ((double) stmt.simpleQueryForLong()) / 1000000;
        } catch (SQLiteDoneException e) {
            return 0.0;
        }
    }
}
