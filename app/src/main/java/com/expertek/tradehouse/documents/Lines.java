package com.expertek.tradehouse.documents;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.line;

import java.util.List;

@Dao
public interface Lines {
    @Query("SELECT * FROM MT_lines")
    List<line> getAll();

    @Query("SELECT * FROM MT_lines WHERE LineID IN (:objIds)")
    List<line> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM MT_lines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //line findByName(String first, String last);

    @Insert
    void insertAll(line... objects);

    @Delete
    void delete(line objects);
}
