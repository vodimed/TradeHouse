package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.user;

@Dao
public interface Users {
    @Query("SELECT * FROM TH_users")
    DataSource.Factory<Integer, user> load();

    @Query("SELECT * FROM TH_users WHERE userID = :ident")
    user get(String ident);
}
