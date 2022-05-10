package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.common.extensions.database.SQLiteSchema;
import com.expertek.tradehouse.documents.entity.Document;

import java.util.Arrays;
import java.util.List;

public class Documents {
    private static final int maxpar = 10;
    private final SQLiteDatabase db;

    public Documents(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Document> load() {
        return new SQLitePager.Factory<>(db, Document.class, "SELECT * FROM MT_documents ORDER BY DocName");
    }

    public DataSource.Factory<Integer, Document> load(String... docType) {
        if ((docType == null) || (docType.length <= 0) || docType[0].equals("*")) return load();

        final String[] docParams = new String[maxpar];
        Arrays.fill(docParams, docType[0]);
        System.arraycopy(docType, 0, docParams, 0, docType.length);

        return new SQLitePager.Factory<>(db, Document.class, "SELECT * FROM MT_documents " +
                "WHERE DocType IN (:t0,:t1,:t2,:t3,:t4,:t5,:t6,:t7,:t8,:t9) ORDER BY DocName", (Object[]) docParams);
    }

    public Document get(String ident) {
        final DataSource<Integer, Document> source = new SQLitePager.Factory<>(db, Document.class,
                "SELECT * FROM MT_documents WHERE DocName = :ident", ident).create();
        final List<Document> result = ((SQLitePager<Document>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public String getMaxId() {
        final SQLiteStatement stmt = db.compileStatement(
                "SELECT IFNULL(MAX(DocName), '0000') FROM MT_documents");
        return stmt.simpleQueryForString();
    }

    public boolean hasDuplicate(String ident) {
        final SQLiteStatement stmt = db.compileStatement(
                "SELECT COUNT(*) > 0 FROM MT_documents WHERE DocName = :DocName");
        stmt.bindString(1, ident);
        return (stmt.simpleQueryForLong() > 0);
    }

    public double sumAllDocs(String... docType) {
        if ((docType == null) || (docType.length <= 0) || docType[0].equals("*")) {
            final SQLiteStatement stmt = db.compileStatement("SELECT SUM(FactSum) * 100 FROM MT_documents");
            return ((double) stmt.simpleQueryForLong()) / 100;
        } else {
            final String[] docParams = new String[maxpar];
            Arrays.fill(docParams, docType[0]);
            System.arraycopy(docType, 0, docParams, 0, docType.length);

            final SQLiteStatement stmt = db.compileStatement("SELECT SUM(FactSum) * 100 FROM MT_documents " +
                    "WHERE DocType IN (:t0,:t1,:t2,:t3,:t4,:t5,:t6,:t7,:t8,:t9)");
            stmt.bindAllArgsAsStrings(docParams);
            return ((double) stmt.simpleQueryForLong()) / 100;
        }
    }

    public void insert(Document... objects) {
        final ContentValues map = new ContentValues();
        for (Document document : objects) {
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
            map.put("StartDate", SQLiteSchema.DateConverter.save(document.StartDate));
            map.put("Flags", document.Flags);
            db.replace("MT_documents", null, map);
        }
    }

    public void delete(Document... objects) {
        final String[] DocName = new String[1];
        for (Document document : objects) {
            DocName[0] = document.DocName;
            db.delete("MT_documents", "DocName = :DocName", DocName);
        }
    }
}
