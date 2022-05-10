package com.expertek.tradehouse.documents.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.Line;

@Dao
public interface Lines {
    @Query("SELECT * FROM MT_lines ORDER BY LineID")
    DataSource.Factory<Integer, Line> load();

    @Query("SELECT * FROM MT_lines WHERE DocName = :docName ORDER BY LineID")
    DataSource.Factory<Integer, Line> load(String docName);

    @Query("SELECT * FROM MT_lines WHERE LineID = :ident")
    Line get(int ident);

    @Query("SELECT IFNULL(MAX(LineID), 0) + 1 FROM MT_lines")
    long getNextId();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Line... objects);

    @Delete
    void delete(Line... objects);
}
