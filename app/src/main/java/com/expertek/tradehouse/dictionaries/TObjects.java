package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Objects;

import java.util.List;

@Dao
public interface TObjects {
    @Query("SELECT * FROM TH_objects")
    List<Objects> getAll();

    @Query("SELECT * FROM TH_objects WHERE obj_code IN (:objIds)")
    List<Objects> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_objects WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Objects2 findByName(String first, String last);

    @Insert
    void insertAll(Objects... objects);

    @Delete
    void delete(Objects objects);
}
