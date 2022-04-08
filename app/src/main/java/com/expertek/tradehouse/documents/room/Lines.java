package com.expertek.tradehouse.documents.room;

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
    DataSource.Factory<Integer, line> load();

    @Query("SELECT * FROM MT_lines WHERE DocName = :docName")
    DataSource.Factory<Integer, line> loadByDocument(String docName);

    @Query("SELECT * FROM MT_lines WHERE LineID = :ident")
    line get(int ident);

    @Query("SELECT IFNULL(MAX(LineID), 0) + 1 FROM MT_lines")
    long getNextId();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(line... objects);

    @Delete
    void delete(line... objects);
}
