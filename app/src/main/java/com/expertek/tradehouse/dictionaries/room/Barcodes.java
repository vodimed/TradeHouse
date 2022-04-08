package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.barcode;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes")
    DataSource.Factory<Integer, barcode> load();

    @Query("SELECT * FROM TH_barcodes WHERE BC = :ident")
    barcode get(String ident);
}
