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

    @Query("SELECT * FROM MT_documents WHERE DocName IN (:ident)")
    DataSource.Factory<Integer, document> get(String... ident);

    //@Query("SELECT * FROM MT_documents WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //document findByName(String first, String last);

    @Query("SELECT MAX(DocName) FROM MT_documents")
    String getMaxId();

    @Query("SELECT * FROM MT_documents WHERE DocType IN (:t0,:t1,:t2,:t3,:t4,:t5,:t6,:t7,:t8,:t9)")
    DataSource.Factory<Integer, document> getDocType(String[] docType);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(document... objects);

    @Delete
    void delete(document... objects);
}
