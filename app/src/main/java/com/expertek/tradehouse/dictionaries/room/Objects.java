package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.object;

@Dao
public interface Objects {
    @Query("SELECT * FROM TH_objects")
    DataSource.Factory<Integer, object> load();

    @Query("SELECT * FROM TH_objects WHERE obj_code IN (:ident)")
    DataSource.Factory<Integer, object> get(int... ident);

    //@Query("SELECT * FROM TH_objects WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Objects2 findByName(String first, String last);

    @Insert
    void insert(object... objects);

    @Delete
    void delete(object... object);
}
