package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.barcode;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes WHERE BC LIKE :ident || '%' OR :ident IS NULL")
    DataSource.Factory<Integer, barcode> load(String ident);

    @Query("SELECT * FROM TH_barcodes WHERE BC = :ident")
    barcode get(String ident);
}
