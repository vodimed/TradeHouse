package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Clients;

import java.util.List;

@Dao
public interface TClients {
    @Query("SELECT * FROM TH_clients")
    List<Clients> getAll();

    @Query("SELECT * FROM TH_clients WHERE cli_code IN (:objIds)")
    List<Clients> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_clients WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Clients findByName(String first, String last);

    @Insert
    void insertAll(Clients... objects);

    @Delete
    void delete(Clients objects);
}
