package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.Markline;

import java.util.List;

public class Marklines {
    private final SQLiteDatabase db;

    public Marklines(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Markline> load() {
        return new SQLitePager.Factory<>(db, Markline.class, "SELECT * FROM MT_MarkLines");
    }

    public DataSource.Factory<Integer, Markline> load(String docName, String partIDTH) {
        return new SQLitePager.Factory<>(db, Markline.class, "SELECT * FROM MT_MarkLines WHERE " +
                "DocName = :docName AND (PartIDTH = :partIDTH OR :partIDTH IS NULL)", docName, partIDTH);
    }

    public Markline get(int ident) {
        final DataSource<Integer, Markline> source = new SQLitePager.Factory<>(db, Markline.class,
                "SELECT * FROM MT_MarkLines WHERE LineID = :ident", ident).create();
        final List<Markline> result = ((SQLitePager<Markline>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public long getNextId() {
        final SQLiteStatement stmt = db.compileStatement("SELECT IFNULL(MAX(LineID), 0) + 1 FROM MT_MarkLines");
        return stmt.simpleQueryForLong();
    }

    public void insert(Markline... objects) {
        final ContentValues map = new ContentValues();
        for (Markline markline : objects) {
            map.clear();
            map.put("LineID", markline.LineID);
            map.put("DocName", markline.DocName);
            map.put("MarkCode", markline.MarkCode);
            map.put("PartIDTH", markline.PartIDTH);
            map.put("Sts", markline.Sts);
            map.put("MarkParent", markline.MarkParent);
            map.put("BoxQnty", markline.BoxQnty);
            db.replace("MT_lines", null, map);
        }
    }

    public void delete(Markline... objects) {
        final String[] LineID = new String[1];
        for (Markline markline : objects) {
            LineID[0] = String.valueOf(markline.LineID);
            db.delete("MT_MarkLines", "LineID = :LineID", LineID);
        }
    }
}
