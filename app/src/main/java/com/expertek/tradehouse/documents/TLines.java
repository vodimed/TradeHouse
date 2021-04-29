package com.expertek.tradehouse.documents;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.Lines;

import java.util.List;

@Dao
public interface TLines {
    @Query("SELECT * FROM MT_lines")
    List<Lines> getAll();

    @Query("SELECT * FROM MT_lines WHERE LineID IN (:objIds)")
    List<Lines> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM MT_lines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Lines findByName(String first, String last);

    @Insert
    void insertAll(Lines... objects);

    @Delete
    void delete(Lines objects);
}
