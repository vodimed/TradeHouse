package com.expertek.tradehouse.dictionaries;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.expertek.tradehouse.dictionaries.entity.Users;

import java.util.List;

@Dao
public interface TUsers {
    @Query("SELECT * FROM TH_users")
    List<Users> getAll();

    @Query("SELECT * FROM TH_users WHERE uid IN (:objIds)")
    List<Users> loadAllByIds(int[] objIds);

    //@Query("SELECT * FROM TH_users WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //Users findByName(String first, String last);

    @Insert
    void insertAll(Users... objects);

    @Delete
    void delete(Users objects);
}
