package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Goods;

import java.util.List;

@Dao
public interface TGoods {
    @Query("SELECT * FROM TH_goods")
    List<Goods> getAll();

    @Query("SELECT * FROM TH_goods WHERE uid IN (:objIds)")
    List<Goods> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_goods WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Goods findByName(String first, String last);

    @Insert
    void insertAll(Goods... objects);

    @Delete
    void delete(Goods objects);
}
