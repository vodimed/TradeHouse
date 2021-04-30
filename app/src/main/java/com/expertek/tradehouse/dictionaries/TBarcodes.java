package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Barcodes;

import java.util.List;

@Dao
public interface TBarcodes {
    @Query("SELECT * FROM TH_barcodes")
    List<Barcodes> getAll();

    @Query("SELECT * FROM TH_barcodes WHERE BC IN (:objIds)")
    List<Barcodes> loadAllByIds(String[] objIds);

    //@Query("SELECT * FROM TH_barcodes WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Barcodes findByName(String first, String last);

    @Insert
    void insertAll(Barcodes... objects);

    @Delete
    void delete(Barcodes objects);
}
