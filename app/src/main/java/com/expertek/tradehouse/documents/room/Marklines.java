package com.expertek.tradehouse.documents.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.Markline;

@Dao
public interface Marklines {
    @Query("SELECT * FROM MT_MarkLines")
    DataSource.Factory<Integer, Markline> load();

    @Query("SELECT * FROM MT_MarkLines WHERE DocName = :docName AND (PartIDTH = :partIDTH OR :partIDTH IS NULL)")
    DataSource.Factory<Integer, Markline> load(String docName, String partIDTH);

    @Query("SELECT * FROM MT_MarkLines WHERE LineID = :ident")
    Markline get(int ident);

    @Query("SELECT IFNULL(MAX(LineID), 0) + 1 FROM MT_MarkLines")
    long getNextId();

    @Insert
    void insert(Markline... objects);

    @Delete
    void delete(Markline... objects);
}
