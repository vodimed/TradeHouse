package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "TH_clients", primaryKeys = {"cli_code", "cli_type"})
public class client {
    @ColumnInfo(name = "cli_code")
    @NonNull
    public int cli_code; // код из ТН

    @ColumnInfo(name = "cli_type")
    @NonNull
    public int cli_type; // тип из ТН

    @ColumnInfo(name = "Name")
    public String Name; // название
}
