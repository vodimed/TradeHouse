package com.expertek.tradehouse.documents.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.expertek.tradehouse.documents.entity.markline;

public class Marklines {
    private final SQLiteDatabase db;

    public Marklines(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, markline> load() {
        return null;
    }

    public DataSource.Factory<Integer, markline> get(int... ident) {
        return null;
    }

    //@Query("SELECT * FROM MT_MarkLines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //markline findByName(String first, String last);

    public void insert(markline... objects) {
        
    }

    public void delete(markline... objects) {
        
    }
}
