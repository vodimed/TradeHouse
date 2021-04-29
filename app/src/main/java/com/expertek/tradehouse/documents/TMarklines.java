package com.expertek.tradehouse.documents;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.Marklines;

import java.util.List;

@Dao
public interface TMarklines {
    @Query("SELECT * FROM MT_MarkLines")
    List<Marklines> getAll();

    @Query("SELECT * FROM MT_MarkLines WHERE LineID IN (:objIds)")
    List<Marklines> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM MT_MarkLines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Marklines findByName(String first, String last);

    @Insert
    void insertAll(Marklines... objects);

    @Delete
    void delete(Marklines objects);
}
