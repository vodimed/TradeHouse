package com.expertek.tradehouse.documents.sqlite;

import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.document;

public class Documents {
    private final SQLiteDatabase db;

    public Documents(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, document> load() {
        return new SQLitePager.Factory<>(db, document.class, "SELECT * FROM MT_documents");
    }

    public DataSource.Factory<Integer, document> get(String... ident) {
        return new SQLitePager.Factory<>(db, document.class, "SELECT * FROM MT_documents WHERE DocName IN (:ident)", (Object) ident);
    }

    //@Query("SELECT * FROM MT_documents WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //document findByName(String first, String last);

    public String getMaxId() {
        final SQLiteStatement stmt = db.compileStatement("SELECT MAX(DocName) FROM MT_documents");
        return stmt.simpleQueryForString();
    }

    public DataSource.Factory<Integer, document> getDocType(String docType) {
        return new SQLitePager.Factory<>(db, document.class, "SELECT * FROM MT_documents WHERE DocType = :docType", docType);
    }

    public void insert(document... objects) {
        
    }

    public void delete(document... objects) {
        
    }
}
