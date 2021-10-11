package com.expertek.tradehouse.documents.sqlite;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.line;

public class Lines {
    private final SQLiteDatabase db;

    public Lines(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, line> load() {
        return new SQLitePager.Factory<>(db, line.class, "SELECT * FROM MT_lines");
    }

    public DataSource.Factory<Integer, line> get(int... ident) {
        return new SQLitePager.Factory<>(db, line.class, "SELECT * FROM MT_lines WHERE LineID IN (:ident)", ident);
    }

    //@Query("SELECT * FROM MT_lines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //line findByName(String first, String last);

    public DataSource.Factory<Integer, line> loadByDocument(String docName) {
        return new SQLitePager.Factory<>(db, line.class, "SELECT * FROM MT_lines WHERE DocName = :docName", docName);
    }

    public void insert(line... objects) {
        
    }

    public void delete(line... objects) {
        
    }
}
