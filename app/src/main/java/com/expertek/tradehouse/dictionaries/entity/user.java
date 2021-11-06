package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

//TODO ROOM: @Entity(tableName = "TH_users")
public class user implements Serializable {
    //TODO ROOM: @PrimaryKey
    public @NonNull String userID = ""; // Ид пользователя из TH
    public String userName; // Имя пользователя в ТН
}
