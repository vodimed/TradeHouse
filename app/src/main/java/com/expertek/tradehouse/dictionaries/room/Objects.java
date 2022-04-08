package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.object;

@Dao
public interface Objects {
    @Query("SELECT * FROM TH_objects")
    DataSource.Factory<Integer, object> load();

    @Query("SELECT * FROM TH_objects WHERE obj_code = :ident")
    object get(int ident);
}
