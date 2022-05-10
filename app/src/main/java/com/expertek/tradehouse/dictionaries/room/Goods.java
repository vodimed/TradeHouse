package com.expertek.tradehouse.dictionaries.room;

import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;

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

    @Query("SELECT Name FROM TH_goods WHERE GoodsID = :ident")
    String getName(int ident);
}
