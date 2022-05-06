package com.expertek.tradehouse.documents.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.Line;

import java.util.List;

public class Lines {
    private final SQLiteDatabase db;

    public Lines(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Line> load() {
        return new SQLitePager.Factory<>(db, Line.class, "SELECT * FROM MT_lines");
    }

    public DataSource.Factory<Integer, Line> load(String docName) {
        return new SQLitePager.Factory<>(db, Line.class, "SELECT * FROM MT_lines WHERE DocName = :docName", docName);
    }

    public Line get(int ident) {
        final DataSource<Integer, Line> source = new SQLitePager.Factory<>(db, Line.class,
                "SELECT * FROM MT_lines WHERE LineID = :ident", ident).create();
        final List<Line> result = ((SQLitePager<Line>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    public long getNextId() {
        final SQLiteStatement stmt = db.compileStatement("SELECT IFNULL(MAX(LineID), 0) + 1 FROM MT_lines");
        return stmt.simpleQueryForLong();
    }

    public void insert(Line... objects) {
        final ContentValues map = new ContentValues();
        for (Line line : objects) {
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

    public void delete(Line... objects) {
        final String[] LineID = new String[1];
        for (Line line : objects) {
            LineID[0] = String.valueOf(line.LineID);
            db.delete("MT_lines", "LineID = :LineID", LineID);
        }
    }
}
