package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Obj;

@Dao
public interface Objs {
    @Query("SELECT * FROM TH_objects")
    DataSource.Factory<Integer, Obj> load();

    @Query("SELECT * FROM TH_objects WHERE obj_code = :ident")
    Obj get(int ident);

    @Query("SELECT MIN(obj_code) FROM TH_objects WHERE obj_type = :objType")
    int getId(String objType);
}
