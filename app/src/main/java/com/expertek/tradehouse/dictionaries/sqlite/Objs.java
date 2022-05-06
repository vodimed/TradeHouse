package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.Obj;

import java.util.List;

public class Objs {
    private final SQLiteDatabase db;

    public Objs(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Obj> load() {
        return new SQLitePager.Factory<>(db, Obj.class, "SELECT * FROM TH_objects");
    }

    public Obj get(int ident) {
        final DataSource<Integer, Obj> source = new SQLitePager.Factory<>(db, Obj.class,
                "SELECT * FROM TH_objects WHERE obj_code = :ident", ident).create();
        final List<Obj> result = ((SQLitePager<Obj>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
