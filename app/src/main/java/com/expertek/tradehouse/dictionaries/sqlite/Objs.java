package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

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

    public int getId(String objType) {
        final SQLiteStatement stmt = db.compileStatement(
                "SELECT MIN(obj_code) FROM TH_objects WHERE obj_type = :objType");
        stmt.bindString(1, objType);
        try {
            return (int) stmt.simpleQueryForLong();
        } catch (SQLiteDoneException e) {
            return 0;
        }
    }
}
