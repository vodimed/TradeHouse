package com.expertek.tradehouse.dictionaries.sqlite;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.barcode;

public class Barcodes {
    private final SQLiteDatabase db;

    public Barcodes(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, barcode> load() {
        return new SQLitePager.Factory<>(db, barcode.class, "SELECT * FROM TH_barcodes");
    }

    public DataSource.Factory<Integer, barcode> get(String... ident) {
        return new SQLitePager.Factory<>(db, barcode.class, "SELECT * FROM TH_barcodes WHERE BC IN (:ident)", (Object) ident);
    }

    //@Query("SELECT * FROM TH_barcodes WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //barcode findByName(String first, String last);

    public void insert(barcode... objects) {

    }

    public void delete(barcode... objects) {

    }
}
