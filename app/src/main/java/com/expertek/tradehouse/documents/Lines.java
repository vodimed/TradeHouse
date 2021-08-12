package com.expertek.tradehouse.documents;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.line;

@Dao
public interface Lines {
    @Query("SELECT * FROM MT_lines")
    DataSource.Factory<Integer, line> getAll();

    @Query("SELECT * FROM MT_lines WHERE LineID IN (:objIds)")
    DataSource.Factory<Integer, line> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM MT_lines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //line findByName(String first, String last);

    @Query("SELECT * FROM MT_lines WHERE DocName = :docName")
    DataSource.Factory<Integer, line> loadByDocument(String docName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(line... objects);

    @Delete
    void delete(line... objects);
}
