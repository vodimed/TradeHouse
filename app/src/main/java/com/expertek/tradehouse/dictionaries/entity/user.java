package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TH_users")
public class user {
    @PrimaryKey
    @ColumnInfo(name = "userID")
    @NonNull
    public String userID; // Ид пользователя из TH

    @ColumnInfo(name = "userName")
    public String userName; // Имя пользователя в ТН
}
