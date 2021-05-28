package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.barcode;

import java.util.List;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes")
    List<barcode> getAll();

    @Query("SELECT * FROM TH_barcodes WHERE BC IN (:objIds)")
    List<barcode> loadAllByIds(String[] objIds);

    //@Query("SELECT * FROM TH_barcodes WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //barcode findByName(String first, String last);

    @Insert
    void insertAll(barcode... objects);

    @Delete
    void delete(barcode objects);
}
