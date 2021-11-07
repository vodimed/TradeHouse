package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.DateConverter;
import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.document;

import java.util.Arrays;

public class Documents {
    private final SQLiteDatabase db;

    public Documents(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, document> load() {
        return new SQLitePager.Factory<>(db, document.class, "SELECT * FROM MT_documents");
    }

    public DataSource.Factory<Integer, document> get(String... ident) {
        return new SQLitePager.Factory<>(db, document.class, "SELECT * FROM MT_documents WHERE DocName IN (:ident)", ident);
    }

    //@Query("SELECT * FROM MT_documents WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //document findByName(String first, String last);

    public String getNextId() {
        final SQLiteStatement stmt = db.compileStatement("SELECT MAX(DocName) FROM MT_documents");
        final String lastId = stmt.simpleQueryForString();
        if (lastId == null) return "0001";

        final char[] nextId = lastId.toCharArray();
        for (int i = nextId.length - 1; i >= 0; i--) {
            if (nextId[i] >= '0' && nextId[i] <= '9') {
                if (++nextId[i] <= '9') return new String(nextId);
                nextId[i] = '0';
            }
        }
        return "1" + (new String(nextId));
    }

    public boolean duplicate(document document) {
        final SQLiteStatement stmt = db.compileStatement(
                "SELECT COUNT(*) FROM MT_documents WHERE DocName = :DocName");
        stmt.bindString(1, document.DocName);
        return (stmt.simpleQueryForLong() > 0);
    }

    public double sumAllDocs(String[] docType) {
        boolean all_docs = false;
        if ((docType == null) || (docType.length <= 0)) all_docs = true;
        for (String elem : docType) {
            if ((elem == null) || (elem.length() <= 0)) all_docs = true;
        }

        if (all_docs) {
            final SQLiteStatement stmt = db.compileStatement(
                    "SELECT SUM(FactSum) * 100 FROM MT_documents");
            return (double) stmt.simpleQueryForLong() / 100;
        } else {
            final String[] docParams = new String[10];
            Arrays.fill(docParams, docType[0]);
            System.arraycopy(docType, 0, docParams, 0, docType.length);

            final SQLiteStatement stmt = db.compileStatement(
                    "SELECT SUM(FactSum) * 100 FROM MT_documents WHERE DocType IN " +
                            "(:t0,:t1,:t2,:t3,:t4,:t5,:t6,:t7,:t8,:t9)");
            stmt.bindAllArgsAsStrings(docParams);
            return (double) stmt.simpleQueryForLong() / 100;
        }
    }

    public DataSource.Factory<Integer, document> getDocType(String[] docType) {
        if ((docType == null) || (docType.length <= 0)) return load();
        for (String elem : docType) {
            if ((elem == null) || (elem.length() <= 0)) return load();
        }

        final String[] docParams = new String[10];
        Arrays.fill(docParams, docType[0]);
        System.arraycopy(docType, 0, docParams, 0, docType.length);

        return new SQLitePager.Factory<>(db, document.class,
               "SELECT * FROM MT_documents WHERE DocType IN " +
                      "(:t0,:t1,:t2,:t3,:t4,:t5,:t6,:t7,:t8,:t9)", docParams);
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
            map.put("StartDate", DateConverter.get(document.StartDate));
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
