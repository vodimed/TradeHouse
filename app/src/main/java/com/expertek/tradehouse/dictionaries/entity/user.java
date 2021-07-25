package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TH_users")
public class user implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "userID")
    @NonNull
    public String userID = ""; // Ид пользователя из TH

    @ColumnInfo(name = "userName")
    public String userName; // Имя пользователя в ТН
}
