package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import com.common.extensions.database.Entity;

import java.io.Serializable;

@Entity(tableName = "TH_clients", primaryKeys = {"cli_code", "cli_type"})
public class Client implements Serializable {
    public int cli_code; // код из ТН
    public @NonNull String cli_type = ""; // тип из ТН
    public String Name; // название
}
