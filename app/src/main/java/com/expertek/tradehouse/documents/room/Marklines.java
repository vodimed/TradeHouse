package com.expertek.tradehouse.documents.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.markline;

@Dao
public interface Marklines {
    @Query("SELECT * FROM MT_MarkLines")
    DataSource.Factory<Integer, markline> load();

    @Query("SELECT * FROM MT_MarkLines WHERE DocName = :docName")
    DataSource.Factory<Integer, markline> loadByDocument(String docName);

    @Query("SELECT * FROM MT_MarkLines WHERE LineID = :ident")
    markline get(int ident);

    @Insert
    void insert(markline... objects);

    @Delete
    void delete(markline... objects);
}
