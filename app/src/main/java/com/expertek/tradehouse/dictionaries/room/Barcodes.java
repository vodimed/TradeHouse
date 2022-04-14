package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.BarInfo;
import com.expertek.tradehouse.dictionaries.entity.barcode;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes")
    DataSource.Factory<Integer, barcode> load();

    @Query("SELECT * FROM TH_barcodes WHERE BC = :ident")
    barcode get(String ident);

    @Query("SELECT TH_barcodes.*, TH_goods.Name FROM TH_barcodes, TH_goods WHERE BC LIKE :ident || '%' AND TH_barcodes.GoodsID = TH_goods.GoodsID")
    DataSource.Factory<Integer, BarInfo> loadInfo(String ident);
}
