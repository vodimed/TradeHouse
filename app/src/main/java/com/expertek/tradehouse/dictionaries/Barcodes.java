package com.expertek.tradehouse.dictionaries;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.barcode;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes")
    DataSource.Factory<Integer, barcode> getAll();

    @Query("SELECT * FROM TH_barcodes WHERE BC IN (:objIds)")
    DataSource.Factory<Integer, barcode> loadAllByIds(String[] objIds);

    //@Query("SELECT * FROM TH_barcodes WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //barcode findByName(String first, String last);

    @Insert
    void insertAll(barcode... objects);

    @Delete
    void delete(barcode objects);
}
