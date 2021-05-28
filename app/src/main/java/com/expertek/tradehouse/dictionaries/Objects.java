package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.object;

import java.util.List;

@Dao
public interface Objects {
    @Query("SELECT * FROM TH_objects")
    List<object> getAll();

    @Query("SELECT * FROM TH_objects WHERE obj_code IN (:objIds)")
    List<object> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_objects WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Objects2 findByName(String first, String last);

    @Insert
    void insertAll(object... objects);

    @Delete
    void delete(object object);
}
