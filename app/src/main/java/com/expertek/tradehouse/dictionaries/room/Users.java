package com.expertek.tradehouse.dictionaries.room;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.User;

@Dao
public interface Users {
    @Query("SELECT * FROM TH_users")
    DataSource.Factory<Integer, User> load();

    @Query("SELECT * FROM TH_users WHERE userID = :ident")
    User get(String ident);
}
