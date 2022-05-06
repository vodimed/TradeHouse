package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Good;

@Dao
public interface Goods {
    @Query("SELECT * FROM TH_goods")
    DataSource.Factory<Integer, Good> load();

    @Query("SELECT * FROM TH_goods WHERE GoodsID = :ident")
    Good get(int ident);
}
