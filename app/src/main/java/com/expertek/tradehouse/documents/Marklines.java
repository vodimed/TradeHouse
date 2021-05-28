package com.expertek.tradehouse.documents;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.markline;

import java.util.List;

@Dao
public interface Marklines {
    @Query("SELECT * FROM MT_MarkLines")
    List<markline> getAll();

    @Query("SELECT * FROM MT_MarkLines WHERE LineID IN (:objIds)")
    List<markline> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM MT_MarkLines WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //markline findByName(String first, String last);

    @Insert
    void insertAll(markline... objects);

    @Delete
    void delete(markline objects);
}
