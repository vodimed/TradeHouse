package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import com.common.extensions.database.Entity;
import com.common.extensions.database.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TH_users")
public class user implements Serializable {
    @PrimaryKey
    public @NonNull String userID = ""; // Ид пользователя из TH
    public String userName; // Имя пользователя в ТН
}
