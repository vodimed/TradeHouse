package com.expertek.tradehouse.documents.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.documents.entity.line;

@Dao
public interface Lines {
    @Query("SELECT * FROM MT_lines")
    DataSource.Factory<Integer, line> load();

    @Query("SELECT * FROM MT_lines WHERE LineID IN (:ident)")
    DataSource.Factory<Integer, line> get(int... ident);

    //@Query("SELECT * FROM MT_lines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //line findByName(String first, String last);

    @Query("SELECT * FROM MT_lines WHERE DocName = :docName")
    DataSource.Factory<Integer, line> loadByDocument(String docName);

    @Query("SELECT * FROM MT_lines WHERE BC = :BC")
    DataSource.Factory<Integer, line> loadByDocument(String BC);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(line... objects);

    @Delete
    void delete(line... objects);
}
