package com.expertek.tradehouse.dictionaries;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.user;

@Dao
public interface Users {
    @Query("SELECT * FROM TH_users")
    DataSource.Factory<Integer, user> getAll();

    @Query("SELECT * FROM TH_users WHERE userID IN (:objIds)")
    DataSource.Factory<Integer, user> loadAllByIds(String[] objIds);

    //@Query("SELECT * FROM TH_users WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //user findByName(String first, String last);

    @Insert
    void insertAll(user... objects);

    @Delete
    void delete(user objects);
}
