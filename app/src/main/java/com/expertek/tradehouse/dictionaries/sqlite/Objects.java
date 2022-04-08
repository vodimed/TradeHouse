package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.object;

import java.util.List;

public class Objects {
    private final SQLiteDatabase db;

    public Objects(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, object> load() {
        return new SQLitePager.Factory<>(db, object.class, "SELECT * FROM TH_objects");
    }

    public object get(int ident) {
        final DataSource<Integer, object> source = new SQLitePager.Factory<>(db, object.class,
                "SELECT * FROM TH_objects WHERE obj_code = :ident", ident).create();
        final List<object> result = ((SQLitePager<object>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
