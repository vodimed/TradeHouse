package com.expertek.tradehouse.documents;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.document;

import java.util.List;

@Dao
public interface Documents {
    @Query("SELECT * FROM MT_documents")
    List<document> getAll();

    @Query("SELECT * FROM MT_documents WHERE DocName IN (:objIds)")
    List<document> loadAllByIds(String[] objIds);

    //@Query("SELECT * FROM MT_documents WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //document findByName(String first, String last);

    @Query("SELECT * FROM MT_documents WHERE DocType = :doctype")
    DataSource.Factory<Integer, document> loadByDocType(String doctype);

    @Insert
    void insertAll(document... objects);

    @Delete
    void delete(document objects);
}
