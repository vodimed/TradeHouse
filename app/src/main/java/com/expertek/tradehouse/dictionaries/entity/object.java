package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "TH_objects", primaryKeys = {"obj_code", "obj_type"})
public class object {
    @ColumnInfo(name = "obj_code")
    @NonNull
    public int obj_code; // код объекта ТН

    @ColumnInfo(name = "obj_type")
    @NonNull
    public String obj_type; // тип объекта ТН

    @ColumnInfo(name = "Name")
    public String Name; // название
}
