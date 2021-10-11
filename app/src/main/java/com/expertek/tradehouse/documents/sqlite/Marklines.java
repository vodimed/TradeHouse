package com.expertek.tradehouse.documents.sqlite;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.markline;

public class Marklines {
    private final SQLiteDatabase db;

    public Marklines(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, markline> load() {
        return new SQLitePager.Factory<>(db, markline.class, "SELECT * FROM MT_MarkLines");
    }

    public DataSource.Factory<Integer, markline> get(int... ident) {
        return new SQLitePager.Factory<>(db, markline.class, "SELECT * FROM MT_MarkLines WHERE LineID IN (:ident)", ident);
    }

    //@Query("SELECT * FROM MT_MarkLines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //markline findByName(String first, String last);

    public void insert(markline... objects) {
        
    }

    public void delete(markline... objects) {
        
    }
}
