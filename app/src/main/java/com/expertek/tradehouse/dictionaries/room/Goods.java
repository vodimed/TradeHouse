package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.good;

@Dao
public interface Goods {
    @Query("SELECT * FROM TH_goods")
    DataSource.Factory<Integer, good> load();

    @Query("SELECT * FROM TH_goods WHERE GoodsID = :ident")
    good get(int ident);
}
