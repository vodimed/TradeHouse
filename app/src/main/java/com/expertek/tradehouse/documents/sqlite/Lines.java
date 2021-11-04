package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;

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

    public DataSource.Factory<Integer, line> findBarCode(String BC) {
        return new SQLitePager.Factory<>(db, line.class, "SELECT * FROM MT_lines WHERE BC = :BC", BC);
    }

    public void insert(line... objects) {
        final ContentValues map = new ContentValues();
        for (line line : objects) {
            map.clear();
            map.put("LineID", line.LineID);
            map.put("DocName", line.DocName);
            map.put("Pos", line.Pos);
            map.put("GoodsID", line.GoodsID);
            map.put("GoodsName", line.GoodsName);
            map.put("UnitBC", line.UnitBC);
            map.put("BC", line.BC);
            map.put("Price", line.Price);
            map.put("DocQnty", line.DocQnty);
            map.put("FactQnty", line.FactQnty);
            map.put("AlcCode", line.AlcCode);
            map.put("PartIDTH", line.PartIDTH);
            map.put("Flags", line.Flags);
            db.replace("MT_lines", null, map);
        }
    }

    public void delete(line... objects) {
        final String[] LineID = new String[1];
        for (line line : objects) {
            LineID[0] = String.valueOf(line.LineID);
            db.delete("MT_lines", "LineID = :LineID", LineID);
        }
    }
}
