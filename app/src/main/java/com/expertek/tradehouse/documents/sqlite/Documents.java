package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.DateConverter;
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

    public DataSource.Factory<Integer, document> getDocType(String[] docType) {
        if (docType == null || docType.length <= 0) return load();

        for (int i = 0; i < docType.length; i++) {
            if (docType[i] == null || docType[i].length() <= 0) return load();
        }

        final String[] docParams = new String[10];
        System.arraycopy(docType, 0, docParams, 0, docType.length);

        return new SQLitePager.Factory<>(db, document.class,
               "SELECT * FROM MT_documents WHERE DocType IN " +
                      "(:t0,:t1,:t2,:t3,:t4,:t5,:t6,:t7,:t8,:t9)", (Object[]) docParams);
    }

    public void insert(document... objects) {
        final ContentValues map = new ContentValues();
        for (document document : objects) {
            map.clear();
            map.put("DocName", document.DocName);
            map.put("DocType", document.DocType);
            map.put("Complete", document.Complete);
            map.put("Status", document.Status);
            map.put("ClientID", document.ClientID);
            map.put("ClientType", document.ClientType);
            map.put("ObjectID", document.ObjectID);
            //map.put("ObjectType", document.ObjectType);
            map.put("UserID", document.UserID);
            map.put("UserName", document.UserName);
            map.put("FactSum", document.FactSum);
            map.put("StartDate", new DateConverter().save(document.StartDate));
            map.put("Flags", document.Flags);
            db.replace("MT_documents", null, map);
        }
    }

    public void delete(document... objects) {
        final String[] DocName = new String[1];
        for (document document : objects) {
            DocName[0] = document.DocName;
            db.delete("MT_documents", "DocName = :DocName", DocName);
        }
    }
}
