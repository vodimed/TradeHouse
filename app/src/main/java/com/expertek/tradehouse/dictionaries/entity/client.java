package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

//TODO ROOM: @Entity(tableName = "TH_clients", primaryKeys = {"cli_code", "cli_type"})
public class client implements Serializable {
    public int cli_code; // код из ТН
    public @NonNull String cli_type = ""; // тип из ТН
    public String Name; // название
}
