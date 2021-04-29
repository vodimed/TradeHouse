package com.expertek.tradehouse.dictionaries.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TH_users")
public class Users {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "userID")
    public int userID; // Ид пользователя из TH

    @ColumnInfo(name = "userName")
    public String userName; // Имя пользователя в ТН
}
