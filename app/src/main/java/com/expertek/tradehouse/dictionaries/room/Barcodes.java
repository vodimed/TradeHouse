package com.expertek.tradehouse.dictionaries.room;

import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Barcode;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes WHERE BC LIKE :ident || '%'")
    DataSource.Factory<Integer, Barcode> load(String ident);

    @Query("SELECT * FROM TH_barcodes WHERE BC = :ident")
    Barcode get(String ident);

    @Query("SELECT UnitRate FROM TH_barcodes WHERE BC = :ident")
    double getRate(String ident);
}
