package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Client;

@Dao
public interface Clients {
    @Query("SELECT * FROM TH_clients")
    DataSource.Factory<Integer, Client> load();

    @Query("SELECT * FROM TH_clients WHERE cli_code = :ident")
    Client get(int ident);
}
