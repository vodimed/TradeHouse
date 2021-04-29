package com.expertek.tradehouse.dictionaries.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TH_objects")
public class Objects {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "obj_type")
    public int obj_type; // тип объекта ТН

    @ColumnInfo(name = "obj_code")
    public int obj_code; // код объекта ТН

    @ColumnInfo(name = "Name")
    public String Name; // название
}
