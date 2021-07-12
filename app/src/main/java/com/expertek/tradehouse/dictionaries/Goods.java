package com.expertek.tradehouse.dictionaries;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.good;

@Dao
public interface Goods {
    @Query("SELECT * FROM TH_goods")
    DataSource.Factory<Integer, good> getAll();

    @Query("SELECT * FROM TH_goods WHERE GoodsID IN (:objIds)")
    DataSource.Factory<Integer, good> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_goods WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //good findByName(String first, String last);

    @Insert
    void insertAll(good... objects);

    @Delete
    void delete(good objects);
}
