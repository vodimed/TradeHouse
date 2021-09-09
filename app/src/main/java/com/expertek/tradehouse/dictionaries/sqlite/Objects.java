package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.object;

public class Objects {
    private final SQLiteDatabase db;

    public Objects(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, object> load() {
        return new SQLitePager.Factory<object>(new object(), db, "SELECT * FROM TH_objects");
    }

    public DataSource.Factory<Integer, object> get(int... ident) {
        return new SQLitePager.Factory<object>(new object(), db, "SELECT * FROM TH_objects WHERE obj_code IN (:ident)", ident);
    }

    //@Query("SELECT * FROM TH_objects WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Objects2 findByName(String first, String last);

    public void insert(object... objects) {
        
    }

    public void delete(object... object) {
        
    }
}
