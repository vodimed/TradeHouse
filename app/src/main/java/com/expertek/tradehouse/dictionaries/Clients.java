package com.expertek.tradehouse.dictionaries;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.client;

@Dao
public interface Clients {
    @Query("SELECT * FROM TH_clients")
    DataSource.Factory<Integer, client> getAll();

    @Query("SELECT * FROM TH_clients WHERE cli_code IN (:objIds)")
    DataSource.Factory<Integer, client> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_clients WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //client findByName(String first, String last);

    @Insert
    void insertAll(client... objects);

    @Delete
    void delete(client objects);
}
