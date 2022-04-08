package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.client;

@Dao
public interface Clients {
    @Query("SELECT * FROM TH_clients")
    DataSource.Factory<Integer, client> load();

    @Query("SELECT * FROM TH_clients WHERE cli_code = :ident")
    client get(int ident);
}
