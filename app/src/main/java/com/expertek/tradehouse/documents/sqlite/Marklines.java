package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.markline;

import java.util.List;

public class Marklines {
    private final SQLiteDatabase db;

    public Marklines(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, markline> load() {
        return new SQLitePager.Factory<>(db, markline.class, "SELECT * FROM MT_MarkLines");
    }

    public DataSource.Factory<Integer, markline> loadByDocument(String docName) {
        return new SQLitePager.Factory<>(db, markline.class, "SELECT * FROM MT_MarkLines WHERE DocName = :docName", docName);
    }

    public markline get(int ident) {
        final DataSource<Integer, markline> source = new SQLitePager.Factory<>(db, markline.class,
                "SELECT * FROM MT_MarkLines WHERE LineID = :ident", ident).create();
        final List<markline> result = ((SQLitePager<markline>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public void insert(markline... objects) {
        final ContentValues map = new ContentValues();
        for (markline markline : objects) {
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

    public void delete(markline... objects) {
        final String[] LineID = new String[1];
        for (markline markline : objects) {
            LineID[0] = String.valueOf(markline.LineID);
            db.delete("MT_MarkLines", "LineID = :LineID", LineID);
        }
    }
}
