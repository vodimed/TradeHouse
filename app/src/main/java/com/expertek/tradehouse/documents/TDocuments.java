package com.expertek.tradehouse.documents;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.Documents;

import java.util.List;

@Dao
public interface TDocuments {
    @Query("SELECT * FROM MT_documents")
    List<Documents> getAll();

    @Query("SELECT * FROM MT_documents WHERE DocName IN (:objIds)")
    List<Documents> loadAllByIds(String[] objIds);

    //@Query("SELECT * FROM MT_documents WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Documents findByName(String first, String last);

    @Insert
    void insertAll(Documents... objects);

    @Delete
    void delete(Documents objects);
}
