package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.barcode;

@Dao
public interface Barcodes {
    @Query("SELECT * FROM TH_barcodes")
    DataSource.Factory<Integer, barcode> load();

    @Query("SELECT * FROM TH_barcodes WHERE BC IN (:ident)")
    DataSource.Factory<Integer, barcode> get(String... ident);

    //@Query("SELECT * FROM TH_barcodes WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //barcode findByName(String first, String last);

    @Insert
    void insert(barcode... objects);

    @Delete
    void delete(barcode... objects);
}
