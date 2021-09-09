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

    @Query("SELECT * FROM MT_MarkLines WHERE LineID IN (:ident)")
    DataSource.Factory<Integer, markline> get(int... ident);

    //@Query("SELECT * FROM MT_MarkLines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //markline findByName(String first, String last);

    @Insert
    void insert(markline... objects);

    @Delete
    void delete(markline... objects);
}
