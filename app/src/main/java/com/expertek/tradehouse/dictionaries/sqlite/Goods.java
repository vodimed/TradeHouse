package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.Good;

import java.util.List;

public class Goods {
    private final SQLiteDatabase db;

    public Goods(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Good> load() {
        return new SQLitePager.Factory<>(db, Good.class, "SELECT * FROM TH_goods");
    }

    public Good get(int ident) {
        final DataSource<Integer, Good> source = new SQLitePager.Factory<>(db, Good.class,
                "SELECT * FROM TH_goods WHERE GoodsID = :ident", ident).create();
        final List<Good> result = ((SQLitePager<Good>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
