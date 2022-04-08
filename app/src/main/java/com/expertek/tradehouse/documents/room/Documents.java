package com.expertek.tradehouse.documents.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.expertek.tradehouse.documents.entity.document;

@Dao
public interface Documents {
    @Query("SELECT * FROM MT_documents")
    DataSource.Factory<Integer, document> load();

    @Query("SELECT * FROM MT_documents WHERE DocType IN (:docType) OR :docType = '*'")
    DataSource.Factory<Integer, document> loadByDocType(String... docType);

    @Query("SELECT * FROM MT_documents WHERE DocName = :ident")
    document get(String ident);

    @Query("SELECT IFNULL(MAX(DocName), '0000') FROM MT_documents")
    String getMaxId();

    @Query("SELECT COUNT(*) > 0 FROM MT_documents WHERE DocName = :ident")
    boolean hasDuplicate(String ident);

    @Query("SELECT SUM(FactSum) FROM MT_documents WHERE DocType IN (:docType) OR :docType = '*'")
    double sumAllDocs(String... docType);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(document... objects);

    @Delete
    void delete(document... objects);
}
