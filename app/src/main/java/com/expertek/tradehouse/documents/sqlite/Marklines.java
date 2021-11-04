package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;

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
