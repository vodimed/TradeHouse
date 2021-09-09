package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.good;

public class Goods {
    private final SQLiteDatabase db;

    public Goods(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, good> load() {
        return new SQLitePager.Factory<good>(new good(), db, "SELECT * FROM TH_goods");
    }

    public DataSource.Factory<Integer, good> get(int... ident) {
        return new SQLitePager.Factory<good>(new good(), db, "SELECT * FROM TH_goods", ident);
    }

    //@Query("SELECT * FROM TH_goods WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //good findByName(String first, String last);

    public void insert(good... objects) {

    }

    public void delete(good... objects) {

    }
}
